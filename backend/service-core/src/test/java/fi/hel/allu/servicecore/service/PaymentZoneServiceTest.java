package fi.hel.allu.servicecore.service;

import org.geolatte.geom.Geometry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.SettableListenableFuture;

import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.LocationJson;
import fi.hel.allu.servicecore.util.AsyncWfsRestTemplate;

import static org.geolatte.geom.builder.DSL.*;
import static org.junit.Assert.assertEquals;

public class PaymentZoneServiceTest {

  private static final Geometry GEOMETRY = geometrycollection(3879, polygon(ring(
      c(25497127.432633813, 6672772.5078439955),
      c(25497127.437172506, 6672778.301429846),
      c(25497143.148493476, 6672778.289155258),
      c(25497143.143979605, 6672772.495569395),
      c(25497127.432633813, 6672772.5078439955))));

  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  private AsyncWfsRestTemplate restTemplate;
  private PaymentZoneService paymentZoneService;

  private static final String PAYMENT_ZONE_PLACEHOLDER = "<paymentzones>";
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
        PAYMENT_ZONE_PLACEHOLDER +
      "</wfs:FeatureCollection>";

  private static final String PAYMENT_ZONE_MEMBER =
      "<gml:featureMember>" +
          "<helsinki:Terassit_maksuvyohyke fid=\"Terassit_maksuvyohyke.2\">" +
            "<helsinki:tietopalvelu_id>2</helsinki:tietopalvelu_id>" +
            "<helsinki:maksuvyohyke>1</helsinki:maksuvyohyke>" +
          "</helsinki:Terassit_maksuvyohyke>" +
      "</gml:featureMember>";

  private static final String URL = "https://geoserver";


  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    paymentZoneService = new PaymentZoneServiceImpl(applicationProperties, restTemplate);
    Mockito.when(applicationProperties.getPaymentClassUrl()).thenReturn(URL);
  }


  @Test
  public void shouldReturnPaymentZone() {
    setResponse(RESPONSE.replace(PAYMENT_ZONE_PLACEHOLDER, PAYMENT_ZONE_MEMBER));
    assertEquals("1", paymentZoneService.getPaymentZone(createLocation(GEOMETRY)));
  }

  @Test
  public void shouldReturnDefaultIfNotInZone() {
    setResponse(RESPONSE.replace(PAYMENT_ZONE_PLACEHOLDER, ""));
    assertEquals("2", paymentZoneService.getPaymentZone(createLocation(GEOMETRY)));
  }

  private void setResponse(String response) {
    SettableListenableFuture<ResponseEntity<String>> future = new SettableListenableFuture<>();
    future.set(ResponseEntity.ok(response));
    Mockito.when(restTemplate.exchange(Mockito.eq(URL), Mockito.eq(HttpMethod.POST), Mockito.any(),
        Mockito.eq(String.class))).thenReturn(future);
  }


  private LocationJson createLocation(Geometry geometry) {
    final LocationJson location = new LocationJson();
    location.setGeometry(geometry);
    return location;
  }

}
