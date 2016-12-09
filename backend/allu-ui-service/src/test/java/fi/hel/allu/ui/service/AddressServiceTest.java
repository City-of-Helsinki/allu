package fi.hel.allu.ui.service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.CoordinateJson;
import fi.hel.allu.ui.domain.PostalAddressJson;
import fi.hel.allu.ui.util.WfsRestTemplate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class AddressServiceTest {

  private static final String GEOCODE_URL = "http://geocode";
  private static final String SEARCH_URL = "http://search";

  private static final String wfsGeocodeXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<wfs:FeatureCollection xmlns=\"http://www.opengis.net/wfs\" xmlns:wfs=\"http://www.opengis.net/wfs\" " +
      "xmlns:gml=\"http://www.opengis.net/gml\" xmlns:helsinki=\"http://www.hel.fi/hel\" " +
      "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
      "xsi:schemaLocation=\"http://www.hel.fi/hel http://kartta.hel.fi/ws/geoserver/helsinki/wfs?service=WFS&amp;version=1.0.0&amp;request=DescribeFeatureType&amp;typeName=helsinki%3AHelsinki_osoiteluettelo " +
      "http://www.opengis.net/wfs http://kartta.hel.fi/ws/geoserver/schemas/wfs/1.0.0/WFS-basic.xsd\">" +
      "<gml:boundedBy><gml:null>unknown</gml:null></gml:boundedBy>" +
      "<gml:featureMember><helsinki:Helsinki_osoiteluettelo fid=\"Helsinki_osoiteluettelo.fid-2b4d8300_1569d8621e5_6dc0\">" +
      "<helsinki:id>58478</helsinki:id><helsinki:katunimi>Viipurinkatu</helsinki:katunimi>" +
      "<helsinki:gatan>Viborgsgatan</helsinki:gatan><helsinki:osoitenumero>10</helsinki:osoitenumero>" +
      "<helsinki:n>6675339</helsinki:n><helsinki:e>25496886</helsinki:e><helsinki:kaupunki>Helsinki</helsinki:kaupunki>" +
      "<helsinki:staden>Helsingfors</helsinki:staden><helsinki:tyyppi>1</helsinki:tyyppi><helsinki:postinumero>00510</helsinki:postinumero>" +
      "<helsinki:postitoimipaikka>Helsinki</helsinki:postitoimipaikka><helsinki:osoitenumero_teksti>10</helsinki:osoitenumero_teksti>" +
      "<helsinki:geom><gml:Point srsName=\"http://www.opengis.net/gml/srs/epsg.xml#3879\">" +
      "<gml:coordinates xmlns:gml=\"http://www.opengis.net/gml\" decimal=\".\" cs=\",\" ts=\" \">25496886,6675339</gml:coordinates>" +
      "</gml:Point></helsinki:geom></helsinki:Helsinki_osoiteluettelo></gml:featureMember>" +
      "<gml:featureMember><helsinki:Helsinki_osoiteluettelo fid=\"Helsinki_osoiteluettelo.fid-2b4d8300_1569d8621e5_6dc0\">" +
      "<helsinki:id>58478</helsinki:id><helsinki:katunimi>Viipurinkatu</helsinki:katunimi>" +
      "<helsinki:gatan>Viborgsgatan</helsinki:gatan><helsinki:osoitenumero>11</helsinki:osoitenumero>" +
      "<helsinki:n>6675339</helsinki:n><helsinki:e>25496886</helsinki:e><helsinki:kaupunki>Helsinki</helsinki:kaupunki>" +
      "<helsinki:staden>Helsingfors</helsinki:staden><helsinki:tyyppi>1</helsinki:tyyppi><helsinki:postinumero>00510</helsinki:postinumero>" +
      "<helsinki:postitoimipaikka>Helsinki</helsinki:postitoimipaikka><helsinki:osoitenumero_teksti>11</helsinki:osoitenumero_teksti>" +
      "<helsinki:geom><gml:Point srsName=\"http://www.opengis.net/gml/srs/epsg.xml#3879\">" +
      "<gml:coordinates xmlns:gml=\"http://www.opengis.net/gml\" decimal=\".\" cs=\",\" ts=\" \">25496886,6675339</gml:coordinates>" +
      "</gml:Point></helsinki:geom></helsinki:Helsinki_osoiteluettelo></gml:featureMember>" +
      "</wfs:FeatureCollection>";

  @Mock
  protected ApplicationProperties applicationProperties;
  @Mock
  protected WfsRestTemplate wfsRestTemplate;
  @Mock
  protected ResponseEntity<String> wfsXmlEntity;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(applicationProperties.getStreetGeocodeUrl()).thenReturn(GEOCODE_URL);
    Mockito.when(applicationProperties.getStreetSearchUrl()).thenReturn(SEARCH_URL);
  }

  @Test
  public void testGeocodeAddress() {
    AddressService addressService = new AddressService(applicationProperties, wfsRestTemplate);
    Mockito.when(wfsXmlEntity.getBody()).thenReturn(wfsGeocodeXml);
    Mockito.when(wfsRestTemplate.exchange(
        Mockito.eq(GEOCODE_URL), Mockito.eq(HttpMethod.GET), Mockito.anyObject(), Mockito.eq(String.class), Mockito.eq("Testikatu"), Mockito.eq("1"))).thenReturn((ResponseEntity<String>) wfsXmlEntity);
    CoordinateJson coordinateJson = addressService.geocodeAddress("Testikatu", 1);
    Assert.assertEquals(25496886, coordinateJson.getX(), 0);
    Assert.assertEquals(6675339, coordinateJson.getY(), 0);
  }

  @Test(expected = NoSuchEntityException.class)
  public void testGeocodeMissingAddress() {
    AddressService addressService = new AddressService(applicationProperties, wfsRestTemplate);
    // use regex to remove the featureMember part of XML to simulate WFS answer for unknown location
    Mockito.when(wfsXmlEntity.getBody()).thenReturn(wfsGeocodeXml.replaceAll("<gml:featureMember>.+</gml:featureMember>", ""));
    Mockito.when(wfsRestTemplate.exchange(
        Mockito.eq(GEOCODE_URL), Mockito.eq(HttpMethod.GET), Mockito.anyObject(), Mockito.eq(String.class), Mockito.eq("Testikatu"), Mockito.eq("1"))).thenReturn((ResponseEntity<String>) wfsXmlEntity);
    addressService.geocodeAddress("Testikatu", 1);
  }

  @Test
  public void testFindMatchingStreet() {
    AddressService addressService = new AddressService(applicationProperties, wfsRestTemplate);
    Mockito.when(wfsXmlEntity.getBody()).thenReturn(wfsGeocodeXml);
    Mockito.when(wfsRestTemplate.exchange(
        Mockito.eq(SEARCH_URL), Mockito.eq(HttpMethod.GET), Mockito.anyObject(), Mockito.eq(String.class), Mockito.eq("Testikatu"))).thenReturn((ResponseEntity<String>) wfsXmlEntity);
    List<PostalAddressJson> postalAddressList = addressService.findMatchingStreet("Testikatu");
    Assert.assertEquals(2, postalAddressList.size());
    Assert.assertEquals("Viipurinkatu 10", postalAddressList.get(0).getStreetAddress());
    Assert.assertEquals("Viipurinkatu 11", postalAddressList.get(1).getStreetAddress());
    Assert.assertEquals("00510", postalAddressList.get(0).getPostalCode());
    Assert.assertEquals("Helsinki", postalAddressList.get(0).getCity());
  }

  @Test
  public void testFindMatchingStreetWithStreetNumber() {
    String streetNumber = "1";
    AddressService addressService = new AddressService(applicationProperties, wfsRestTemplate);
    Mockito.when(wfsXmlEntity.getBody()).thenReturn(wfsGeocodeXml);
    Mockito.when(wfsRestTemplate.exchange(
        Mockito.eq(SEARCH_URL), Mockito.eq(HttpMethod.GET), Mockito.anyObject(), Mockito.eq(String.class), Mockito.eq("Testikatu"))).thenReturn((ResponseEntity<String>) wfsXmlEntity);
    List<PostalAddressJson> postalAddressList = addressService.findMatchingStreet("Testikatu " + streetNumber);
    Assert.assertEquals(2, postalAddressList.size());
    Assert.assertEquals("Viipurinkatu 10", postalAddressList.get(0).getStreetAddress());
    Assert.assertEquals("Viipurinkatu 11", postalAddressList.get(1).getStreetAddress());
    Assert.assertEquals("00510", postalAddressList.get(0).getPostalCode());
    Assert.assertEquals("Helsinki", postalAddressList.get(0).getCity());
  }

  @Test
  public void testFindMatchingExactStreetWithStreetNumber() {
    String streetNumber = "10";
    AddressService addressService = new AddressService(applicationProperties, wfsRestTemplate);
    Mockito.when(wfsXmlEntity.getBody()).thenReturn(wfsGeocodeXml);
    Mockito.when(wfsRestTemplate.exchange(
        Mockito.eq(SEARCH_URL), Mockito.eq(HttpMethod.GET), Mockito.anyObject(), Mockito.eq(String.class), Mockito.eq("Testikatu"))).thenReturn((ResponseEntity<String>) wfsXmlEntity);
    List<PostalAddressJson> postalAddressList = addressService.findMatchingStreet("Testikatu " + streetNumber);
    Assert.assertEquals(1, postalAddressList.size());
    Assert.assertEquals("Viipurinkatu 10", postalAddressList.get(0).getStreetAddress());
    Assert.assertEquals("00510", postalAddressList.get(0).getPostalCode());
    Assert.assertEquals("Helsinki", postalAddressList.get(0).getCity());
  }
}
