package fi.hel.allu.servicecore.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryCollection;
import org.geolatte.geom.GeometryType;
import org.geolatte.geom.PointCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import fi.hel.allu.common.wfs.WfsUtil;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.LocationJson;
import fi.hel.allu.servicecore.util.AsyncWfsRestTemplate;

public abstract class AbstractWfsPaymentDataService {

  private static final String COORDINATES = "<coordinates>";
  private static final String REQUEST =
      "<wfs:GetFeature xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
        "xsi:schemaLocation=\"http://www.opengis.net/wfs\" " +
        "xmlns:gml=\"http://www.opengis.net/gml\" xmlns:wfs=\"http://www.opengis.net/wfs\" " +
        "xmlns:ogc=\"http://www.opengis.net/ogc\" service=\"WFS\" version=\"1.0.0\">" +
        "<wfs:Query typeName=\"helsinki:%s\">" +
          "<ogc:PropertyName>%s</ogc:PropertyName>" +
          "<ogc:Filter>" +
            "<ogc:Intersects>" +
              "<ogc:PropertyName>geom</ogc:PropertyName>" +
                "<gml:Polygon srsName=\"http://www.opengis.net/gml/srs/epsg.xml#3879\">" +
                  "<gml:outerBoundaryIs>" +
                    "<gml:LinearRing>" +
                      "<gml:coordinates xmlns:gml=\"http://www.opengis.net/gml\" decimal=\".\" cs=\",\" ts=\" \">" +
                        COORDINATES +
                      "</gml:coordinates>" +
                    "</gml:LinearRing>" +
                  "</gml:outerBoundaryIs>" +
                "</gml:Polygon>" +
            "</ogc:Intersects>" +
          "</ogc:Filter>" +
        "</wfs:Query>" +
      "</wfs:GetFeature>";

  protected static final String UNDEFINED = "undefined";

  private final ApplicationProperties applicationProperties;
  private final AsyncWfsRestTemplate restTemplate;

  private static final Logger logger = LoggerFactory.getLogger(AbstractWfsPaymentDataService.class);

  protected AbstractWfsPaymentDataService(ApplicationProperties applicationProperties, AsyncWfsRestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }

  protected abstract String parseResult(List<String> responses);
  protected abstract String getFeatureTypeName();
  protected abstract String getFeaturePropertyName();

  protected String executeWfsRequest(LocationJson location) {
    final List<String> coordinateArray = getCoordinates(location);
    final List<String> requests = coordinateArray.stream()
        .map(c -> getRequest().replaceFirst(COORDINATES, c)).collect(Collectors.toList());
    try {
      final List<ListenableFuture<ResponseEntity<String>>> responseFutures = sendRequests(requests);
      final List<String> responses = collectResponses(responseFutures);
      return parseResult(responses);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return UNDEFINED;
    }
  }

  private String getRequest() {
    return String.format(REQUEST, getFeatureTypeName(), getFeaturePropertyName());
  }

  private List<ListenableFuture<ResponseEntity<String>>> sendRequests(List<String> requests) {
    final HttpHeaders headers = WfsUtil.createAuthHeaders(
        applicationProperties.getPaymentClassUsername(),
        applicationProperties.getPaymentClassPassword());
    final List<ListenableFuture<ResponseEntity<String>>> responseFutures = new ArrayList<>();
    requests.stream().map((request) -> new HttpEntity<>(request, headers))
      .map((requestEntity) -> restTemplate.exchange(
          applicationProperties.getPaymentClassUrl(),
          HttpMethod.POST,
          requestEntity,
          String.class)).forEachOrdered(response -> responseFutures.add(response));
    return responseFutures;
  }

  private List<String> collectResponses(List<ListenableFuture<ResponseEntity<String>>> responses)
      throws InterruptedException, ExecutionException {
    final List<String> listOfResponses = new ArrayList<>();
    for (ListenableFuture<ResponseEntity<String>> future: responses) {
      final String respBody = future.get().getBody();
      listOfResponses.add(respBody);
    }
    return listOfResponses;
  }

  private List<String> getCoordinates(LocationJson location) {
    if (location.getGeometry().getGeometryType() == GeometryType.GEOMETRY_COLLECTION) {
      final GeometryCollection gc = (GeometryCollection)location.getGeometry();
      final List<String> coordinateArray = new ArrayList<>();
      for (int i = 0; i < gc.getNumGeometries(); i++) {
        coordinateArray.add(getCoordinates(gc.getGeometryN(i)));
      }
      return coordinateArray;
    } else {
      return Arrays.asList(getCoordinates(location.getGeometry()));
    }
  }

  private String getCoordinates(Geometry geometry) {
    final PointCollection points = geometry.getPoints();
    final StringBuilder coordinatesBuilder = new StringBuilder();
    for (int i = 0; i < points.size(); i++) {
      if (coordinatesBuilder.length() > 0) {
        coordinatesBuilder.append(" ");
      }
      coordinatesBuilder.append(String.format("%f", points.getX(i))).append(",").append(String.format("%f", points.getY(i)));
    }
    return coordinatesBuilder.toString();
  }

}