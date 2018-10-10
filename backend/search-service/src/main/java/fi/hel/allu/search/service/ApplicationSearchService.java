package fi.hel.allu.search.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.common.geo.builders.ShapeBuilders;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.PointCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.vividsolutions.jts.geom.Coordinate;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.SearchException;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ApplicationQueryParameters;
import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.search.domain.util.CustomerAnonymizer;

@Service
public class ApplicationSearchService extends GenericSearchService<ApplicationES, ApplicationQueryParameters> {

  @Autowired
  public ApplicationSearchService(
      ElasticSearchMappingConfig elasticSearchMappingConfig,
      Client client,
      ApplicationIndexConductor applicationIndexConductor) {
    super(elasticSearchMappingConfig,
        client,
        ElasticSearchMappingConfig.APPLICATION_TYPE_NAME,
        applicationIndexConductor,
        a -> a.getId().toString(),
        ApplicationES.class);
  }

  public Page<ApplicationES> findApplicationByField(ApplicationQueryParameters queryParameters, Pageable pageRequest, Boolean matchAny) {
    if (pageRequest == null) {
      pageRequest = DEFAULT_PAGEREQUEST;
    }
    try {
      SearchRequestBuilder srBuilder = buildSearchRequest(queryParameters, pageRequest, matchAny);
      SearchResponse response = srBuilder.execute().actionGet();
      return createResult(pageRequest, response);
    } catch (IOException e) {
      throw new SearchException(e);
    }
  }

  protected Page<ApplicationES> createResult(Pageable pageRequest, SearchResponse response) throws IOException {
    long totalHits = Optional.ofNullable(response).map(r -> r.getHits().getTotalHits()).orElse(0L);
    List<ApplicationES> results = (totalHits == 0) ? Collections.emptyList() : iterateSearchResponse(response);
    anonymizeCustomers(results);
    return new PageImpl<>(results, pageRequest, totalHits);
  }

  @Override
  protected void addAdditionalQueryParameters(BoolQueryBuilder qb, ApplicationQueryParameters queryParameters) {
    qb.mustNot(
        QueryBuilders.matchQuery(
            QueryParameter.FIELD_NAME_APPLICATION_STATUS, StatusType.REPLACED.name()).operator(Operator.AND)
    );
    if (queryParameters.getIntersectingGeometry() != null) {
      addGeometryParameter(queryParameters.getIntersectingGeometry(), qb);
    }
  }

  private void addGeometryParameter(Geometry intersectingGeometry, BoolQueryBuilder qb) {
    PointCollection points = intersectingGeometry.getPoints();
    List<Coordinate> coordinates = new ArrayList<>();
    for (int i = 0; i < points.size(); i++) {
      coordinates.add(new Coordinate(points.getX(i), points.getY(i)));
    }
    try {
      QueryBuilder geomQb = QueryBuilders.geoShapeQuery("locations.searchGeometry", ShapeBuilders.newPolygon(coordinates)).relation(ShapeRelation.INTERSECTS);
      qb.must(geomQb);
    } catch (IOException ex) {
      throw new SearchException(ex);
    }
  }

  private void anonymizeCustomers(List<ApplicationES> results) {
    results.forEach(a -> CustomerAnonymizer.anonymize(a.getCustomers()));
  }

  private List<ApplicationES> iterateSearchResponse(SearchResponse response) throws IOException {
    List<ApplicationES> appList = new ArrayList<>();
    if (response != null) {
      for (SearchHit hit : response.getHits()) {
        appList.add(objectMapper.readValue(hit.getSourceAsString(), ApplicationES.class));
      }
    }
    return appList;
  }

}
