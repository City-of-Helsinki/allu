package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.wfs.WfsUtil;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.LocationJson;
import fi.hel.allu.servicecore.service.geocode.PaymentClassXml;
import fi.hel.allu.servicecore.util.AsyncWfsRestTemplate;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryCollection;
import org.geolatte.geom.GeometryType;
import org.geolatte.geom.PointCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutionException;

@Profile("!DEV")
@Service
public class PaymentClassServiceImpl implements PaymentClassService {

  private static final String COORDINATES = "<coordinates>";
  private static final String REQUEST =
    "<wfs:GetFeature xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
      "xsi:schemaLocation=\"http://www.opengis.net/wfs\" " +
      "xmlns:gml=\"http://www.opengis.net/gml\" xmlns:wfs=\"http://www.opengis.net/wfs\" " +
      "xmlns:ogc=\"http://www.opengis.net/ogc\" service=\"WFS\" version=\"1.0.0\">" +
      "<wfs:Query typeName=\"helsinki:Allu_maksuvyohykkeet_testi\">" +
        "<ogc:PropertyName>maksuluokka</ogc:PropertyName>" +
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

  private static final Logger logger = LoggerFactory.getLogger(PaymentClassServiceImpl.class);
  private final ApplicationProperties applicationProperties;
  private final AsyncWfsRestTemplate restTemplate;

  @Autowired
  public PaymentClassServiceImpl(ApplicationProperties applicationProperties, AsyncWfsRestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }

  @Override
  public String getPaymentClass(LocationJson location) {
    final List<String> coordinateArray = getCoordinates(location);
    final List<String> requests = coordinateArray.stream()
        .map(c -> REQUEST.replaceFirst(COORDINATES, c)).collect(Collectors.toList());

    try {
      final List<ListenableFuture<ResponseEntity<String>>> responseFutures = sendRequests(requests);
      final List<String> responses = collectResponses(responseFutures);
      return parsePaymentClass(responses);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new RuntimeException("paymentClass.error");
    }
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

  private String parsePaymentClass(List<String> responses) {
    String paymentClass = "4a"; // If payment tariff undefined -> default to lowest
    for (String response : responses) {
      final PaymentClassXml paymentClassXml = WfsUtil.unmarshalWfs(response, PaymentClassXml.class);
      final List<PaymentClassXml.FeatureMember> paymentClasses = paymentClassXml.featureMember.stream()
          .sorted(Comparator.comparing(f -> f.paymentClass.getPaymentClass()))
          .collect(Collectors.toList());
      if (!paymentClasses.isEmpty()) {
        final String pc = paymentClasses.get(0).paymentClass.getPaymentClass();
        if (pc.compareTo(paymentClass) < 0) {
          paymentClass = pc;
        }
      }
    }
    return paymentClass;
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
