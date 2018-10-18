package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.wfs.WfsUtil;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.LocationJson;
import fi.hel.allu.servicecore.service.geocode.PaymentClassXml;
import fi.hel.allu.servicecore.util.WfsRestTemplate;
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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
  private final WfsRestTemplate restTemplate;

  @Autowired
  public PaymentClassServiceImpl(ApplicationProperties applicationProperties, WfsRestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.restTemplate.setDefaultResponseContentType("text/xml; subtype=\"gml/2.1.2\"");
  }

  @Override
  public String getPaymentClass(LocationJson location) {
    final HttpHeaders headers = WfsUtil.createAuthHeaders(
        applicationProperties.getPaymentClassUsername(),
        applicationProperties.getPaymentClassPassword());
    final String request = REQUEST.replaceFirst(COORDINATES, getCoordinates(location));
    final HttpEntity<String> requestEntity = new HttpEntity<>(request, headers);


    try {
      final ResponseEntity<String> response = restTemplate.exchange(
          applicationProperties.getPaymentClassUrl(),
          HttpMethod.POST,
          requestEntity,
          String.class);
      final PaymentClassXml paymentClass = WfsUtil.unmarshalWfs(response.getBody(), PaymentClassXml.class);
      final List<PaymentClassXml.FeatureMember> paymentClasses = paymentClass.featureMember.stream()
          .sorted(Comparator.comparing(f -> f.paymentClass.getPaymentClass()))
          .collect(Collectors.toList());
      if (paymentClasses.isEmpty()) {
        return "4a"; // Payment tariff undefined -> default to lowest
      } else {
        return paymentClasses.get(0).paymentClass.getPaymentClass();
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new RuntimeException("paymentClass.error");
    }
  }

  private String getCoordinates(LocationJson location) {
    final PointCollection points = location.getGeometry().getPoints();
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
