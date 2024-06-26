package fi.hel.allu.ui.service;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.servicecore.domain.CoordinateJson;
import fi.hel.allu.servicecore.domain.PostalAddressJson;
import fi.hel.allu.servicecore.util.WfsRestTemplate;
import fi.hel.allu.ui.config.ApplicationProperties;

public class AddressServiceTest {

  private static final String GEOCODE_URL = "http://geocode";
  private static final String SEARCH_URL = "http://search";

  private static final String wfsGeocodeXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<wfs:FeatureCollection xmlns=\"http://www.opengis.net/wfs\" xmlns:wfs=\"http://www.opengis.net/wfs\" " +
      "xmlns:gml=\"http://www.opengis.net/gml\" xmlns:avoindata=\"https://www.hel.fi/avoindata\" " +
      "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
      "xsi:schemaLocation=\"https://www.hel.fi/hel https://kartta.hel.fi/ws/geoserver/helsinki/wfs?service=WFS&amp;version=1.0.0&amp;request=DescribeFeatureType&amp;typeName=helsinki%3AHelsinki_osoiteluettelo " +
      "http://www.opengis.net/wfs http://kartta.hel.fi/ws/geoserver/schemas/wfs/1.0.0/WFS-basic.xsd\">" +
      "<gml:boundedBy><gml:null>unknown</gml:null></gml:boundedBy>" +
      "<gml:featureMember><avoindata:Helsinki_osoiteluettelo fid=\"Helsinki_osoiteluettelo.fid-2b4d8300_1569d8621e5_6dc0\">" +
      "<avoindata:id>58478</avoindata:id><avoindata:katunimi>Viipurinkatu</avoindata:katunimi>" +
      "<avoindata:gatan>Viborgsgatan</avoindata:gatan><avoindata:osoitenumero>10</avoindata:osoitenumero>" +
      "<avoindata:n>6675339</avoindata:n><avoindata:e>25496886</avoindata:e><avoindata:kaupunki>Helsinki</avoindata:kaupunki>" +
      "<avoindata:staden>Helsingfors</avoindata:staden><avoindata:tyyppi>1</avoindata:tyyppi><avoindata:postinumero>00510</avoindata:postinumero>" +
      "<avoindata:postitoimipaikka>Helsinki</avoindata:postitoimipaikka><avoindata:osoitenumero_teksti>10</avoindata:osoitenumero_teksti>" +
      "<avoindata:geom><gml:Point srsName=\"http://www.opengis.net/gml/srs/epsg.xml#3879\">" +
      "<gml:coordinates xmlns:gml=\"http://www.opengis.net/gml\" decimal=\".\" cs=\",\" ts=\" \">25496886,6675339</gml:coordinates>" +
      "</gml:Point></avoindata:geom></avoindata:Helsinki_osoiteluettelo></gml:featureMember>" +
      "<gml:featureMember><avoindata:Helsinki_osoiteluettelo fid=\"Helsinki_osoiteluettelo.fid-2b4d8300_1569d8621e5_6dc0\">" +
      "<avoindata:id>58478</avoindata:id><avoindata:katunimi>Viipurinkatu</avoindata:katunimi>" +
      "<avoindata:gatan>Viborgsgatan</avoindata:gatan><avoindata:osoitenumero>11</avoindata:osoitenumero>" +
      "<avoindata:n>6675339</avoindata:n><avoindata:e>25496886</avoindata:e><avoindata:kaupunki>Helsinki</avoindata:kaupunki>" +
      "<avoindata:staden>Helsingfors</avoindata:staden><avoindata:tyyppi>1</avoindata:tyyppi><avoindata:postinumero>00510</avoindata:postinumero>" +
      "<avoindata:postitoimipaikka>Helsinki</avoindata:postitoimipaikka><avoindata:osoitenumero_teksti>11</avoindata:osoitenumero_teksti>" +
      "<avoindata:geom><gml:Point srsName=\"http://www.opengis.net/gml/srs/epsg.xml#3879\">" +
      "<gml:coordinates xmlns:gml=\"http://www.opengis.net/gml\" decimal=\".\" cs=\",\" ts=\" \">25496886,6675339</gml:coordinates>" +
      "</gml:Point></avoindata:geom></avoindata:Helsinki_osoiteluettelo></gml:featureMember>" +
      "</wfs:FeatureCollection>";

  @Mock
  protected ApplicationProperties applicationProperties;
  @Mock
  protected WfsRestTemplate wfsRestTemplate;
  @Mock
  protected ResponseEntity<String> wfsXmlEntity;
  private AddressService addressService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(applicationProperties.getStreetGeocodeUrl()).thenReturn(GEOCODE_URL);
    Mockito.when(applicationProperties.getStreetSearchUrl()).thenReturn(SEARCH_URL);
    addressService = new AddressService(applicationProperties, wfsRestTemplate);
  }

  @Test
  public void testGeocodeAddress() {
    URI uri = UriComponentsBuilder.fromUriString(GEOCODE_URL + "?cql_filter=(katunimi='Testikatu' AND osoitenumero_teksti='1')")
            .build().encode().toUri();

    Mockito.when(wfsXmlEntity.getBody()).thenReturn(wfsGeocodeXml);
    Mockito.when(wfsRestTemplate.exchange(
        Mockito.eq(uri), Mockito.eq(HttpMethod.GET), Mockito.any(), Mockito.eq(String.class))).thenReturn(wfsXmlEntity);

    CoordinateJson coordinateJson = addressService.geocodeAddress("Testikatu", Optional.of("1"));
    Assert.assertEquals(25496886, coordinateJson.getX(), 0);
    Assert.assertEquals(6675339, coordinateJson.getY(), 0);
  }

  @Test
  public void testGeocodeAddressWithLetter() {
    URI uri = UriComponentsBuilder.fromUriString(GEOCODE_URL + "?cql_filter=(katunimi='Testikatu' AND osoitenumero_teksti='1a')")
            .build().encode().toUri();

    Mockito.when(wfsXmlEntity.getBody()).thenReturn(wfsGeocodeXml);
    Mockito.when(wfsRestTemplate.exchange(
        Mockito.eq(uri), Mockito.eq(HttpMethod.GET), Mockito.any(), Mockito.eq(String.class))).thenReturn(wfsXmlEntity);
    CoordinateJson coordinateJson = addressService.geocodeAddress("Testikatu", Optional.of("1a"));
    Assert.assertEquals(25496886, coordinateJson.getX(), 0);
    Assert.assertEquals(6675339, coordinateJson.getY(), 0);
  }

  @Test(expected = NoSuchEntityException.class)
  public void testGeocodeMissingAddress() {
    URI uri = UriComponentsBuilder.fromUriString(GEOCODE_URL + "?cql_filter=(katunimi='Testikatu' AND osoitenumero_teksti='1')")
            .build().encode().toUri();

    // use regex to remove the featureMember part of XML to simulate WFS answer for unknown location
    Mockito.when(wfsXmlEntity.getBody()).thenReturn(wfsGeocodeXml.replaceAll("<gml:featureMember>.+</gml:featureMember>", ""));
    Mockito.when(wfsRestTemplate.exchange(
        Mockito.eq(uri), Mockito.eq(HttpMethod.GET), Mockito.any(), Mockito.eq(String.class))).thenReturn(wfsXmlEntity);
    addressService.geocodeAddress("Testikatu", Optional.of("1"));
  }

  @Test
  public void testFindMatchingStreet() {
    Mockito.when(wfsXmlEntity.getBody()).thenReturn(wfsGeocodeXml);
    Mockito.when(wfsRestTemplate.exchange(
        Mockito.eq(SEARCH_URL), Mockito.eq(HttpMethod.GET), Mockito.any(), Mockito.eq(String.class), Mockito.eq("Testikatu"))).thenReturn(wfsXmlEntity);
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
    Mockito.when(wfsXmlEntity.getBody()).thenReturn(wfsGeocodeXml);
    Mockito.when(wfsRestTemplate.exchange(
        Mockito.eq(SEARCH_URL), Mockito.eq(HttpMethod.GET), Mockito.any(), Mockito.eq(String.class), Mockito.eq("Testikatu"))).thenReturn(wfsXmlEntity);
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
    Mockito.when(wfsXmlEntity.getBody()).thenReturn(wfsGeocodeXml);
    Mockito.when(wfsRestTemplate.exchange(
        Mockito.eq(SEARCH_URL), Mockito.eq(HttpMethod.GET), Mockito.any(), Mockito.eq(String.class), Mockito.eq("Testikatu"))).thenReturn(wfsXmlEntity);
    List<PostalAddressJson> postalAddressList = addressService.findMatchingStreet("Testikatu " + streetNumber);
    Assert.assertEquals(1, postalAddressList.size());
    Assert.assertEquals("Viipurinkatu 10", postalAddressList.get(0).getStreetAddress());
    Assert.assertEquals("00510", postalAddressList.get(0).getPostalCode());
    Assert.assertEquals("Helsinki", postalAddressList.get(0).getCity());
  }

  @Test
  public void testFindMatchingExactStreetWithStreetNumberAndLetter() {
    final String streetNumber = "10";
    final String streetLetter = "a";
    Mockito.when(wfsXmlEntity.getBody()).thenReturn(wfsGeocodeXml);
    Mockito.when(wfsRestTemplate.exchange(
        Mockito.eq(SEARCH_URL), Mockito.eq(HttpMethod.GET), Mockito.any(), Mockito.eq(String.class), Mockito.eq("Testikatu"))).thenReturn(wfsXmlEntity);
    List<PostalAddressJson> postalAddressList = addressService.findMatchingStreet("Testikatu " + streetNumber + streetLetter);
    Assert.assertEquals(1, postalAddressList.size());
    Assert.assertEquals("Viipurinkatu 10", postalAddressList.get(0).getStreetAddress());
    Assert.assertEquals("00510", postalAddressList.get(0).getPostalCode());
    Assert.assertEquals("Helsinki", postalAddressList.get(0).getCity());
  }
}
