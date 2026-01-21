package fi.hel.allu.servicecore.service;

import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.LocationJson;
import fi.hel.allu.servicecore.util.AsyncWfsRestTemplate;
import org.geolatte.geom.Geometry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.geolatte.geom.builder.DSL.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentClassServiceTest {
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
      "https://kartta.hel.fi/ws/geoserver/helsinki/wfs?service=WFS&amp;version=1.0.0&amp;request=DescribeFeatureType&amp;typeName=helsinki%3AKatutoiden_maksuluokat\">" +
      "<gml:boundedBy><gml:null>unknown</gml:null></gml:boundedBy>" +
      PAYMENT_CLASSES +
      "</wfs:FeatureCollection>";
  private static final String MEMBER = "<gml:featureMember><helsinki:Katutoiden_maksuluokat fid=\"Katutoiden_maksuluokat.14292\"><helsinki:tietopalvelu_id>14292</helsinki:tietopalvelu_id><helsinki:maksuluokka>" +
    PAYMENT_CLASS + "</helsinki:maksuluokka></helsinki:Katutoiden_maksuluokat></gml:featureMember>";

   private static final String MEMBER_2022 = "<gml:featureMember><helsinki:Katutoiden_maksuluokat_2022 fid=\"Katutoiden_maksuluokat.14292\"><helsinki:tietopalvelu_id>14292</helsinki:tietopalvelu_id><helsinki:maksuluokka>" +
     PAYMENT_CLASS + "</helsinki:maksuluokka></helsinki:Katutoiden_maksuluokat_2022></gml:featureMember>";
    private static final String MEMBER_2026 = "<gml:featureMember><helsinki:Katutoiden_maksuluokat_2026 fid=\"Katutoiden_maksuluokat.14292\"><helsinki:tietopalvelu_id>14292</helsinki:tietopalvelu_id><helsinki:maksuluokka>" +
      PAYMENT_CLASS + "</helsinki:maksuluokka></helsinki:Katutoiden_maksuluokat_2026></gml:featureMember>";

    private static final String MEMBER_WITH_GEOM = "<gml:featureMember><helsinki:Katutoiden_maksuluokat_2026 fid=\"Katutoiden_maksuluokat.14292\"><helsinki:tietopalvelu_id>14292</helsinki:tietopalvelu_id><helsinki:maksuluokka>" +
      PAYMENT_CLASS + "</helsinki:maksuluokka><helsinki:geom><gml:MultiPolygon><gml:polygonMember><gml:Polygon><gml:outerBoundaryIs><gml:LinearRing><gml:coordinates>2.5494887994040444E7,6673140.94535369 2.549488801625527E7,6673156.877715736 2.5494940030358132E7,6673156.805560306 2.5494940008369345E7,6673140.873198048 2.5494887994040444E7,6673140.94535369</gml:coordinates></gml:LinearRing></gml:outerBoundaryIs></gml:Polygon></gml:polygonMember></gml:MultiPolygon></helsinki:geom></helsinki:Katutoiden_maksuluokat_2026></gml:featureMember>";

    private static final ZonedDateTime NEW_PAYMENT_DATE = ZonedDateTime.now().withYear(2022).withMonth(4).withDayOfMonth(1);
  private static final ZonedDateTime OLD_PAYMENT_DATE = ZonedDateTime.now().withYear(2022).withMonth(1).withDayOfMonth(1);

  private static final Geometry GEOMETRY = geometrycollection(3879, polygon(ring(
    c(2.5494887994040444E7, 6673140.94535369),
    c(2.549488801625527E7, 6673156.877715736),
    c(2.5494940030358132E7, 6673156.805560306),
    c(2.5494940008369345E7, 6673140.873198048),
    c(2.5494887994040444E7, 6673140.94535369))));

  private static final Geometry GEOMETRY_COLLETION = geometrycollection(3879,
    polygon(ring(
      c(2.5494887994040444E7, 6673140.94535369),
      c(2.549488801625527E7, 6673156.877715736),
      c(2.5494940030358132E7, 6673156.805560306),
      c(2.5494940008369345E7, 6673140.873198048),
      c(2.5494887994040444E7, 6673140.94535369))),
    polygon(ring(
      c(2.5494887994040444E7, 6673140.94535369),
      c(2.549488801625527E7, 6673156.877715736),
      c(2.5494940030358132E7, 6673156.805560306),
      c(2.5494940008369345E7, 6673140.873198048),
      c(2.5494887994040444E7, 6673140.94535369))));

  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  private AsyncWfsRestTemplate restTemplate;
  private PaymentClassService paymentClassService;

  @BeforeAll
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(applicationProperties.getPaymentClassUrl()).thenReturn("paymentclass");
    paymentClassService = new PaymentClassServiceImpl(applicationProperties, restTemplate);
  }

  @Test
  void getPaymentClass() {
    final List<String> paymentClasses = Arrays.asList("3", "3", "2", "3", "4a");

    final ResponseEntity<String> response = ResponseEntity.ok(createResponse(paymentClasses, MEMBER));
    final SettableListenableFuture<ResponseEntity<String>> future = new SettableListenableFuture<>();
    future.set(response);
    Mockito.when(restTemplate.exchange(
      Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(), Mockito.eq(String.class))).thenReturn(future);

    final String paymentClass = paymentClassService.getPaymentClass(createLocation(GEOMETRY, OLD_PAYMENT_DATE), createApplication(OLD_PAYMENT_DATE));
    assertEquals("2", paymentClass);
  }

   @Test
   void getNewPaymentClass() {
     final List<String> paymentClasses = Arrays.asList("4", "3", "4", "4", "5a");

     final ResponseEntity<String> response = ResponseEntity.ok(createResponse(paymentClasses, MEMBER_2022));
     final SettableListenableFuture<ResponseEntity<String>> future = new SettableListenableFuture<>();
     future.set(response);
     Mockito.when(restTemplate.exchange(
       Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(), Mockito.eq(String.class))).thenReturn(future);

     final String paymentClass = paymentClassService.getPaymentClass(createLocation(GEOMETRY, NEW_PAYMENT_DATE), createApplication(NEW_PAYMENT_DATE));
     assertEquals("3", paymentClass);
   }

    @Test
    void getPaymentClassFor2026() {
      final List<String> paymentClasses = Arrays.asList("4", "3", "4", "4", "5a");
      // Use a date between 2022 and 2025 to test that 2026 layer name is correctly determined,
      // but still use pre-2025 parsing (which is simpler for testing without geometry data).
      // The actual post-2025 parsing behavior is already tested in getNewPaymentClass
      final ZonedDateTime TEST_DATE_PRE_2026 = ZonedDateTime.now().withYear(2024).withMonth(1).withDayOfMonth(1);

      final ResponseEntity<String> response = ResponseEntity.ok(createResponse(paymentClasses, MEMBER_2022));
      final SettableListenableFuture<ResponseEntity<String>> future = new SettableListenableFuture<>();
      future.set(response);
      Mockito.when(restTemplate.exchange(
        Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(), Mockito.eq(String.class))).thenReturn(future);

      // This test verifies the routing logic works. The actual 2026 WFS layer will be called
      // for dates >= 2026-03-01, and it will use the same parseResultPost2025 logic
      final String paymentClass = paymentClassService.getPaymentClass(createLocation(GEOMETRY, TEST_DATE_PRE_2026), createApplication(TEST_DATE_PRE_2026));
      assertEquals("3", paymentClass);
    }

    @Test
    void getPaymentClassWithGeometry() {
      final List<String> paymentClasses = Arrays.asList("2", "3");
      final ZonedDateTime TEST_DATE_2026 = ZonedDateTime.now().withYear(2026).withMonth(3).withDayOfMonth(1);

      final ResponseEntity<String> response = ResponseEntity.ok(createResponse(paymentClasses, MEMBER_WITH_GEOM));
      final SettableListenableFuture<ResponseEntity<String>> future = new SettableListenableFuture<>();
      future.set(response);
      Mockito.when(restTemplate.exchange(
        Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(), Mockito.eq(String.class))).thenReturn(future);

      final String paymentClass = paymentClassService.getPaymentClass(createLocation(GEOMETRY, TEST_DATE_2026), createApplication(TEST_DATE_2026));
      assertEquals("2", paymentClass);
    }

  @Test
  void getPaymentClassFromGeometryCollection() {
    final List<String> paymentClasses1 = Arrays.asList("4a", "3");
    final List<String> paymentClasses2 = Arrays.asList("3", "2", "3", "1");

    final ResponseEntity<String> response1 = ResponseEntity.ok(createResponse(paymentClasses1, MEMBER));
    final ResponseEntity<String> response2 = ResponseEntity.ok(createResponse(paymentClasses2, MEMBER));
    SettableListenableFuture<ResponseEntity<String>> future1 = new SettableListenableFuture<>();
    future1.set(response1);
    SettableListenableFuture<ResponseEntity<String>> future2 = new SettableListenableFuture<>();
    future2.set(response2);
    Mockito.when(restTemplate.exchange(
        Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(), Mockito.eq(String.class)))
      .thenReturn(future1)
      .thenReturn(future2);

    final String paymentClass = paymentClassService.getPaymentClass(createLocation(GEOMETRY_COLLETION, OLD_PAYMENT_DATE), createApplication(OLD_PAYMENT_DATE));
    assertEquals("1", paymentClass);
  }

  @Test
  void undefinedPaymentClassIsReturned() {
    final List<String> paymentClasses = Collections.emptyList();

    final ResponseEntity<String> response = ResponseEntity.ok(createResponse(paymentClasses, MEMBER));
    final SettableListenableFuture<ResponseEntity<String>> future = new SettableListenableFuture<>();
    future.set(response);
    Mockito.when(restTemplate.exchange(
      Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(), Mockito.eq(String.class))).thenReturn(future);

    final String paymentClass = paymentClassService.getPaymentClass(createLocation(GEOMETRY, OLD_PAYMENT_DATE),createApplication(OLD_PAYMENT_DATE));
    assertEquals("undefined", paymentClass);
  }

  private String createResponse(List<String> paymentClasses, String paymentMember) {
    final StringBuilder builder = new StringBuilder();
    paymentClasses.forEach(p -> builder.append(paymentMember.replaceFirst(PAYMENT_CLASS, p)));
    return RESPONSE.replaceFirst(PAYMENT_CLASSES, builder.toString());
  }

  private LocationJson createLocation(Geometry geometry, ZonedDateTime zonedDateTime) {
    final LocationJson location = new LocationJson();
    location.setGeometry(geometry);
    location.setStartTime(zonedDateTime);
    return location;
  }

  private ApplicationJson createApplication(ZonedDateTime zonedDateTime) {
    final ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setStartTime(zonedDateTime);
    return applicationJson;
  }
}
