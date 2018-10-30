package fi.hel.allu.servicecore.service;

import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.LocationJson;
import fi.hel.allu.servicecore.util.AsyncWfsRestTemplate;
import org.geolatte.geom.Geometry;
import static org.geolatte.geom.builder.DSL.c;
import static org.geolatte.geom.builder.DSL.geometrycollection;
import static org.geolatte.geom.builder.DSL.polygon;
import static org.geolatte.geom.builder.DSL.ring;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.Arrays;
import java.util.List;;

public class PaymentClassServiceTest {
  private static final String PAYMENT_CLASSES = "<payment_classes>";
  private static final String PAYMENT_CLASS = "<payment_class>";
  private static final String RESPONSE = 
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
      "<wfs:FeatureCollection xmlns=\"http://www.opengis.net/wfs\" " +
          "xmlns:wfs=\"http://www.opengis.net/wfs\" " +
          "xmlns:gml=\"http://www.opengis.net/gml\" " +
          "xmlns:helsinki=\"https://www.hel.fi/hel\" " +
          "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
          "xsi:schemaLocation=\"http://www.opengis.net/wfs " +
          "http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd " +
          "https://www.hel.fi/hel " +
          "https://kartta.hel.fi/ws/geoserver/wfs?service=WFS&amp;version=1.0.0&amp;request=DescribeFeatureType&amp;typeName=helsinki%3AAllu_maksuvyohykkeet_testi\">" +
        "<gml:boundedBy><gml:null>unknown</gml:null></gml:boundedBy>" +
        PAYMENT_CLASSES +
      "</wfs:FeatureCollection>";
  private static final String MEMBER = "<gml:featureMember><helsinki:Allu_maksuvyohykkeet_testi fid=\"Allu_maksuvyohykkeet_testi.14292\"><helsinki:tietopalvelu_id>14292</helsinki:tietopalvelu_id><helsinki:maksuluokka>" + 
      PAYMENT_CLASS + "</helsinki:maksuluokka></helsinki:Allu_maksuvyohykkeet_testi></gml:featureMember>";

  private static final Geometry GEOMETRY = geometrycollection(3879, polygon(ring(
      c(2.5494887994040444E7,6673140.94535369),
      c(2.549488801625527E7,6673156.877715736),
      c(2.5494940030358132E7,6673156.805560306),
      c(2.5494940008369345E7,6673140.873198048),
      c(2.5494887994040444E7,6673140.94535369))));

  private static final Geometry GEOMETRY_COLLETION = geometrycollection(3879,
      polygon(ring(
          c(2.5494887994040444E7,6673140.94535369),
          c(2.549488801625527E7,6673156.877715736),
          c(2.5494940030358132E7,6673156.805560306),
          c(2.5494940008369345E7,6673140.873198048),
          c(2.5494887994040444E7,6673140.94535369))),
      polygon(ring(
          c(2.5494887994040444E7,6673140.94535369),
          c(2.549488801625527E7,6673156.877715736),
          c(2.5494940030358132E7,6673156.805560306),
          c(2.5494940008369345E7,6673140.873198048),
          c(2.5494887994040444E7,6673140.94535369))));

  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  private AsyncWfsRestTemplate restTemplate;
  private PaymentClassService paymentClassService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    paymentClassService = new PaymentClassServiceImpl(applicationProperties, restTemplate);
  }

  @Test
  public void getPaymentClass() {
    final List<String> paymentClasses = Arrays.asList("3", "3", "2", "3", "4a");

    final ResponseEntity<String> response = ResponseEntity.ok(createResponse(paymentClasses));
    final SettableListenableFuture<ResponseEntity<String>> future = new SettableListenableFuture<>();
    future.set(response);
    Mockito.when(restTemplate.exchange(
        Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.anyObject(), Mockito.eq(String.class))).thenReturn(future);

    final String paymentClass = paymentClassService.getPaymentClass(createLocation(GEOMETRY));
    assertEquals("2", paymentClass);
  }

  @Test
  public void getPaymentClassFromGeometryCollection() {
    final List<String> paymentClasses1 = Arrays.asList("4a", "3");
    final List<String> paymentClasses2 = Arrays.asList("3", "2", "3", "1");

    final ResponseEntity<String> response1 = ResponseEntity.ok(createResponse(paymentClasses1));
    final ResponseEntity<String> response2 = ResponseEntity.ok(createResponse(paymentClasses2));
    SettableListenableFuture<ResponseEntity<String>> future1 = new SettableListenableFuture<>();
    future1.set(response1);
    SettableListenableFuture<ResponseEntity<String>> future2 = new SettableListenableFuture<>();
    future2.set(response2);
    Mockito.when(restTemplate.exchange(
        Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.anyObject(), Mockito.eq(String.class)))
            .thenReturn(future1)
            .thenReturn(future2);

    final String paymentClass = paymentClassService.getPaymentClass(createLocation(GEOMETRY_COLLETION));
    assertEquals("1", paymentClass);
  }

  private String createResponse(List<String> paymentClasses) {
    final StringBuilder builder = new StringBuilder();
    paymentClasses.forEach(p -> builder.append(MEMBER.replaceFirst(PAYMENT_CLASS, p)));
    String r = RESPONSE.replaceFirst(PAYMENT_CLASSES, builder.toString());
    return r;
  }

  private LocationJson createLocation(Geometry geometry) {
    final LocationJson location = new LocationJson();
    location.setGeometry(geometry);
    return location;
  }
}
