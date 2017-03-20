package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.FixedLocation;
import fi.hel.allu.model.domain.FixedLocationArea;
import fi.hel.allu.model.domain.FixedLocationSection;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.FixedLocationAreaJson;
import fi.hel.allu.ui.domain.FixedLocationJson;
import fi.hel.allu.ui.domain.LocationJson;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.geolatte.geom.builder.DSL.c;
import static org.geolatte.geom.builder.DSL.geometrycollection;
import static org.geolatte.geom.builder.DSL.ring;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LocationServiceTest {
  private static Validator validator;
  protected LocationService locationService;
  protected RestTemplate restTemplate;

  private static final int APPLICATION_ID = 12345;

  @BeforeClass
  public static void setUpBeforeClass() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Before
  public void setUp() {
    restTemplate = Mockito.mock(RestTemplate.class);
    locationService = new LocationService(Mockito.mock(ApplicationProperties.class), restTemplate);

    Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(), Mockito.eq(Location[].class)))
        .thenAnswer(
            (Answer<Location[]>) invocation -> {
              // Use given parameter to create a response
              Location location = (Location) invocation.getArgumentAt(1, List.class).get(0);
              location.setId(102);
              return new Location[] { location };
            });

    Mockito.when(restTemplate.exchange(Mockito.any(String.class), Mockito.anyObject(), Mockito.anyObject(), Mockito.eq(Location[].class)))
        .thenAnswer(
            (Answer<ResponseEntity>) invocation -> {
              // Use given parameter to create a response
              HttpEntity<List<Location>> httpEntity = invocation.getArgumentAt(2, HttpEntity.class);
              ResponseEntity<Location[]> responseEntity = Mockito.mock(ResponseEntity.class);
              Mockito.when(responseEntity.getBody()).thenReturn(((List<Location>) httpEntity.getBody()).toArray(new Location[0]));
              return responseEntity;
            });

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Location.class), Mockito.anyInt()))
        .thenAnswer((Answer<ResponseEntity<Location>>) invocation -> createMockLocationResponse(null));
  }

  @Test
  public void testValidationWithValidLocation() {
    Set<ConstraintViolation<LocationJson>> constraintViolations =
        validator.validate(MockServices.createLocationJson(1));
    assertEquals(0, constraintViolations.size());
  }

  @Test
  public void createValidLocation() {
    LocationJson locationJson =
        locationService.createLocations(APPLICATION_ID, Collections.singletonList(MockServices.createLocationJson(null))).get(0);
    assertNotNull(locationJson);
    assertNotNull(locationJson.getId());
    assertEquals(102, locationJson.getId().intValue());
    assertNotNull(locationJson.getPostalAddress());
    assertEquals("city, Json", locationJson.getPostalAddress().getCity());
    assertEquals("33333, Json", locationJson.getPostalAddress().getPostalCode());
    assertEquals("address, Json", locationJson.getPostalAddress().getStreetAddress());
    assertNotNull(locationJson.getGeometry());
    assertEquals(3879, locationJson.getGeometry().getSRID());
  }

  @Test
  public void createLocationWithId() {
    LocationJson locationJsonRequest = MockServices.createLocationJson(1);
    LocationJson locationJson = locationService.createLocations(APPLICATION_ID, Collections.singletonList(locationJsonRequest)).get(0);
    assertNotNull(locationJson);
    assertNotNull(locationJson.getId());
    assertEquals(102, locationJson.getId().intValue());
    assertNotNull(locationJson.getPostalAddress());
    assertEquals("city, Json", locationJson.getPostalAddress().getCity());
    assertEquals("33333, Json", locationJson.getPostalAddress().getPostalCode());
    assertEquals("address, Json", locationJson.getPostalAddress().getStreetAddress());
    assertNotNull(locationJson.getGeometry());
    assertEquals(3879, locationJson.getGeometry().getSRID());
  }

  @SuppressWarnings("unchecked") // Needed for Mockito invocation.getArgumentAt()
  @Test
  public void updateValidLocation() {
    Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(),
        Mockito.eq(Location.class), Mockito.anyInt())).thenAnswer(
            (Answer<ResponseEntity<Location>>) invocation -> createMockLocationResponse(
                invocation.getArgumentAt(2, HttpEntity.class)));
    LocationJson locationJson = MockServices.createLocationJson(1);
    locationJson = locationService.createLocations(APPLICATION_ID, Collections.singletonList(locationJson)).get(0);
    assertNotNull(locationJson);
    assertNotNull(locationJson.getId());
    assertEquals(102, locationJson.getId().intValue());
    assertNotNull(locationJson.getPostalAddress());
    assertEquals("city, Json", locationJson.getPostalAddress().getCity());
    assertEquals("33333, Json", locationJson.getPostalAddress().getPostalCode());
    assertEquals("address, Json", locationJson.getPostalAddress().getStreetAddress());
    assertNotNull(locationJson.getGeometry());
    assertEquals(3879, locationJson.getGeometry().getSRID());
  }

  @Test
  public void updateLocationWithoutId() {
    LocationJson locationJson = MockServices.createLocationJson(null);
    locationJson = locationService.createLocations(APPLICATION_ID, Collections.singletonList(locationJson)).get(0);
    assertNotNull(locationJson);
    assertNotNull(locationJson.getId());
    assertEquals(102, locationJson.getId().intValue());
    assertNotNull(locationJson.getPostalAddress());
    assertEquals("city, Json", locationJson.getPostalAddress().getCity());
    assertEquals("33333, Json", locationJson.getPostalAddress().getPostalCode());
    assertEquals("address, Json", locationJson.getPostalAddress().getStreetAddress());
    assertNotNull(locationJson.getGeometry());
    assertEquals(3879, locationJson.getGeometry().getSRID());
  }

  @Test
  public void testFindById() {
    LocationJson locationJson = locationService.findLocationById(102);
    assertNotNull(locationJson);
    assertNotNull(locationJson.getId());
    assertEquals(102, locationJson.getId().intValue());
    assertNotNull(locationJson.getPostalAddress());
    assertEquals("City1, Model", locationJson.getPostalAddress().getCity());
    assertEquals("33333, Model", locationJson.getPostalAddress().getPostalCode());
    assertEquals("Street 1, Model", locationJson.getPostalAddress().getStreetAddress());
    assertNotNull(locationJson.getGeometry());
    assertEquals(3879, locationJson.getGeometry().getSRID());
  }

  @Test
  public void testGetFixedLocationList() {
    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(FixedLocation[].class)))
        .then(invocation -> createMockFixedLocationList());

    List<FixedLocationJson> fixedLocationList = locationService.getFixedLocationList();
    assertEquals(2, fixedLocationList.size());
    assertEquals("FixedLocation 0", fixedLocationList.get(0).getArea());
  }

  @Test
  public void testGetFixedLocationAreaList() {
    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(FixedLocationArea[].class)))
        .then(invocation -> createMockFixedLocationAreaList());

    List<FixedLocationAreaJson> areaList = locationService.getFixedLocationAreaList();
    assertEquals(2, areaList.size());
    assertEquals("FixedLocation 0", areaList.get(0).getName());
    assertEquals("Section 0", areaList.get(0).getSections().get(0).getName());
  }

  private ResponseEntity<Location> createMockLocationResponse(HttpEntity<Location> request) {
    return new ResponseEntity<>(createMockLocationModel(request == null ? null : request.getBody()), HttpStatus.OK);
  }

  private static Location createMockLocationModel(Location input) {
    if (input != null && input.getId() != null) {
      return input;
    }
    Location location = new Location();
    location.setCity("City1, Model");
    location.setPostalCode("33333, Model");
    location.setStreetAddress("Street 1, Model");
    location.setFixedLocationIds(Arrays.asList(23456, 7656));
    location.setId(102);
    location.setGeometry(geometrycollection(3879, ring(c(0, 0), c(0, 1), c(1, 1), c(1, 0), c(0, 0))));
    return location;
  }

  private ResponseEntity<FixedLocation[]> createMockFixedLocationList() {
    FixedLocation[] fixedLocations = new FixedLocation[2];
    for (int i = 0; i < fixedLocations.length; ++i) {
      FixedLocation fixedLocation = new FixedLocation();
      fixedLocation.setId(911 + i);
      fixedLocation.setArea("FixedLocation " + i);
      fixedLocation.setSection("Section " + i);
      fixedLocations[i] = fixedLocation;
    }
    return new ResponseEntity<>(fixedLocations, HttpStatus.OK);
  }

  private ResponseEntity<FixedLocationArea[]> createMockFixedLocationAreaList() {
    FixedLocationArea[] flas = new FixedLocationArea[2];
    for (int i = 0; i < flas.length; ++i) {
      FixedLocationArea fixedLocationArea = new FixedLocationArea();
      fixedLocationArea.setId(911 + i);
      fixedLocationArea.setName("FixedLocation " + i);
      FixedLocationSection fls = new FixedLocationSection();
      fls.setSection("Section " + i);
      fls.setId(9110 + i);
      fixedLocationArea.setSections(Collections.singletonList(fls));
      flas[i] = fixedLocationArea;
    }
    return new ResponseEntity<>(flas, HttpStatus.OK);
  }
}
