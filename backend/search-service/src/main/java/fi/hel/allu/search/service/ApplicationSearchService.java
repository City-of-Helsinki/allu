package fi.hel.allu.search.service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.search.domain.LocationES;
import fi.hel.allu.search.indexConductor.ApplicationIndexConductor;
import org.apache.commons.lang3.BooleanUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.common.geo.builders.*;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.PointCollection;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.SearchException;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ApplicationQueryParameters;
import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.search.domain.util.CustomerAnonymizer;

@Service
public class ApplicationSearchService extends GenericSearchService<ApplicationES, ApplicationQueryParameters> {

  private static final String[] RESPONSE_FILTERED_FIELDS = new String[] {"locations.searchGeometry", "applicationTypeData"};

  @Autowired
  public ApplicationSearchService(
      ElasticSearchMappingConfig elasticSearchMappingConfig,
      RestHighLevelClient client,
      ApplicationIndexConductor applicationIndexConductor) {
    super(elasticSearchMappingConfig,
          client,
          applicationIndexConductor,
          a -> a.getId().toString(),
          ApplicationES.class);
  }

  public Page<ApplicationES> findApplicationByField(ApplicationQueryParameters queryParameters, Pageable pageRequest, Boolean matchAny) {
    if (pageRequest == null) {
      pageRequest = DEFAULT_PAGEREQUEST;
    }
    try {
      SearchSourceBuilder srBuilder = buildSearchRequest(queryParameters, pageRequest, matchAny);
      SearchResponse response = executeSearchRequest(srBuilder);
      return createResult(pageRequest, response, queryParameters.getZoom());
    } catch (IOException e) {
      throw new SearchException(e);
    }
  }

  protected Page<ApplicationES> createResult(Pageable pageRequest, SearchResponse response, Integer zoom) throws IOException {
    long totalHits = Optional.ofNullable(response).map(r -> r.getHits().getTotalHits()).orElse(0L);
    List<ApplicationES> results = (totalHits == 0) ? Collections.emptyList() : iterateSearchResponse(response, zoom);
    anonymizeCustomers(results);
    return new PageImpl<>(results, pageRequest, totalHits);
  }

  @Override
  protected BoolQueryBuilder addAdditionalQueryParameters(BoolQueryBuilder qb, ApplicationQueryParameters queryParameters) {
    if (!queryParameters.isIncludeArchived()) {
      qb.mustNot(
          QueryBuilders.matchQuery(
              QueryParameter.FIELD_NAME_APPLICATION_STATUS, StatusType.REPLACED.name()).operator(Operator.AND)
          );
    }
    if (queryParameters.getIntersectingGeometry() != null) {
      addGeometryParameter(queryParameters.getIntersectingGeometry(), qb);
    }
    if (BooleanUtils.isTrue(queryParameters.getHasProject())) {
      qb.must(QueryBuilders.existsQuery("project"));
    }

    return addOrSurveyRequired(qb, queryParameters);
  }

  @Override
  protected SearchSourceBuilder addFieldFilter(SearchSourceBuilder srBuilder) {
    return srBuilder.fetchSource(null, RESPONSE_FILTERED_FIELDS);
  }

  private void addGeometryParameter(Geometry intersectingGeometry, BoolQueryBuilder qb) {
    PointCollection points = intersectingGeometry.getPoints();
    List<Coordinate> coordinates = new ArrayList<>();
    for (int i = 0; i < points.size(); i++) {
      coordinates.add(new Coordinate(points.getX(i), points.getY(i)));
    }
    CoordinatesBuilder coordinateBuilder = new CoordinatesBuilder();
    coordinateBuilder.coordinates(coordinates);
    PolygonBuilder polygonBuilder = new PolygonBuilder(coordinateBuilder);
    try {
      QueryBuilder geomQb = QueryBuilders.geoShapeQuery("locations.searchGeometry", polygonBuilder).relation(ShapeRelation.INTERSECTS);
      qb.must(geomQb);
    } catch (IOException ex) {
      throw new SearchException(ex);
    }
  }

  private BoolQueryBuilder addOrSurveyRequired(BoolQueryBuilder qb, ApplicationQueryParameters queryParameters) {
    if (BooleanUtils.isTrue(queryParameters.getSurveyRequired())) {
      MatchQueryBuilder surveyRequired = QueryBuilders.matchQuery("applicationTags", ApplicationTagType.SURVEY_REQUIRED.name());
      return should(surveyRequired, qb);
    } else {
      return qb;
    }
  }

  private void anonymizeCustomers(List<ApplicationES> results) {
    results.forEach(a -> CustomerAnonymizer.anonymize(a.getCustomers()));
  }

  private List<ApplicationES> iterateSearchResponse(SearchResponse response, Integer zoom) throws IOException {
    List<ApplicationES> appList = new ArrayList<>();
    if (response != null) {
      for (SearchHit hit : response.getHits()) {
        ApplicationES applicationES = objectMapper.readValue(hit.getSourceAsString(), ApplicationES.class);
        applicationES.setLocations(getBestLocationRepresentations(applicationES.getLocations(), zoom));
        appList.add(applicationES);
      }
    }
    return appList;
  }

  /**
   * Remove redundant locations from lists by either getting the location with full geometries or
   * grabbing the one location whose geometries suit best with the provided zoom level.
   * @param locationESList List of all the locations saved in elasticsearch
   * @param zoom The zoom level to get the optimal geometries
   * @return The locations best suited for the situation
   */
  private List<LocationES> getBestLocationRepresentations(List<LocationES> locationESList, Integer zoom) {
    // Bundle to lists grouped by locationKey
    List<List<LocationES>> locationESLists = locationESList.stream()
      .map(location -> locationESList.stream()
        .filter(l -> l.getLocationKey().equals(location.getLocationKey())).collect(Collectors.toList()))
      .distinct().collect(Collectors.toList());
    List<LocationES> cleanedLocationESList = locationESLists.stream()
      .map(locations -> getLocationByZoom(locations, zoom))
      .collect(Collectors.toList());
    if (!cleanedLocationESList.contains(null)) {
      return cleanedLocationESList;
    }
    return locationESList;
  }

  /**
   * Remove redundant locations from lists by either getting the location with full geometries or
   * grabbing the one location whose geometries suit best with the provided zoom level.
   * @param locationESList List of locations with (hopefully) the same locationKey
   * @param zoom The zoom level to get the optimal geometries
   * @return The location best suited for the situation
   */
  private LocationES getLocationByZoom(List<LocationES> locationESList, Integer zoom) {
    if (zoom != null) {
      LocationES locationES = locationESList.stream()
        .filter(l -> l.getZoom() != null && l.getZoom() <= zoom)
        .max(Comparator.comparingInt(LocationES::getZoom))
        .orElse(null);
      if (locationES != null) {
        return locationES;
      }
    }
    return locationESList.stream()
      .filter(l -> l.getZoom() != null)
      .max(Comparator.comparingInt(LocationES::getZoom))
      .orElse(locationESList.isEmpty() ? null : locationESList.get(0)); // Return the first item if nothing works
  }

}