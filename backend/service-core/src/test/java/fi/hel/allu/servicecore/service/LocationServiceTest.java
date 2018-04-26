package fi.hel.allu.servicecore.service;

import fi.hel.allu.model.domain.*;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.FixedLocationAreaJson;
import fi.hel.allu.servicecore.domain.FixedLocationJson;
import fi.hel.allu.servicecore.domain.LocationJson;
import fi.hel.allu.servicecore.domain.UserJson;
import org.junit.Assert;
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

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.geolatte.geom.builder.DSL.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;

public class LocationServiceTest {
  private static Validator validator;
  protected LocationService locationService;
  protected RestTemplate restTemplate;
  protected UserService userService;
  private UserJson testUser;

  private static final int APPLICATION_ID = 12345;

  @BeforeClass
  public static void setUpBeforeClass() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Before
  public void setUp() {
    restTemplate = Mockito.mock(RestTemplate.class);
    ApplicationProperties properties = Mockito.mock(ApplicationProperties.class);
    userService = Mockito.mock(UserService.class);
    locationService = new LocationService(properties, restTemplate, userService);

    testUser = new UserJson();
    testUser.setId(1);
    Mockito.when(userService.getCurrentUser()).thenReturn(testUser);
    Mockito.when(properties.getFixedLocationUrl()).thenReturn("http://fixedlocations");

    Mockito.when(restTemplate.postForObject(any(String.class), anyObject(), eq(Location[].class), eq(testUser.getId())))
        .thenAnswer(invocation -> {
          // Use given parameter to create a response
          Location location = (Location) invocation.getArgumentAt(1, List.class).get(0);
          location.setId(102);
          return new Location[] { location };
        });

    Mockito.when(restTemplate.exchange(any(String.class), anyObject(), anyObject(), eq(Location[].class)))
        .thenAnswer(invocation -> {
          // Use given parameter to create a response
          HttpEntity<List<Location>> httpEntity = invocation.getArgumentAt(2, HttpEntity.class);
          ResponseEntity<Location[]> responseEntity = Mockito.mock(ResponseEntity.class);
          Mockito.when(responseEntity.getBody()).thenReturn(((List<Location>) httpEntity.getBody()).toArray(new Location[0]));
          return responseEntity;
        });
    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Location.class), Mockito.anyInt()))
        .thenAnswer(invocation -> createMockLocationResponse(null));
  }

  @Test
  public void testValidationWithValidLocation() {
    Set<ConstraintViolation<LocationJson>> constraintViolations =
        validator.validate(MockServices.createLocationJson(1));
    Assert.assertEquals(0, constraintViolations.size());
  }

  @Test
  public void createValidLocation() {
    LocationJson locationJson =
        locationService.createLocations(APPLICATION_ID, Collections.singletonList(MockServices.createLocationJson(null))).get(0);
    Assert.assertNotNull(locationJson);
    Assert.assertNotNull(locationJson.getId());
    Assert.assertEquals(102, locationJson.getId().intValue());
    Assert.assertNotNull(locationJson.getPostalAddress());
    Assert.assertEquals("city, Json", locationJson.getPostalAddress().getCity());
    Assert.assertEquals("33333, Json", locationJson.getPostalAddress().getPostalCode());
    Assert.assertEquals("address, Json", locationJson.getPostalAddress().getStreetAddress());
    Assert.assertNotNull(locationJson.getGeometry());
    Assert.assertEquals(3879, locationJson.getGeometry().getSRID());
  }

  @Test
  public void createLocationWithId() {
    LocationJson locationJsonRequest = MockServices.createLocationJson(1);
    LocationJson locationJson = locationService.createLocations(APPLICATION_ID, Collections.singletonList(locationJsonRequest)).get(0);
    Assert.assertNotNull(locationJson);
    Assert.assertNotNull(locationJson.getId());
    Assert.assertEquals(102, locationJson.getId().intValue());
    Assert.assertNotNull(locationJson.getPostalAddress());
    Assert.assertEquals("city, Json", locationJson.getPostalAddress().getCity());
    Assert.assertEquals("33333, Json", locationJson.getPostalAddress().getPostalCode());
    Assert.assertEquals("address, Json", locationJson.getPostalAddress().getStreetAddress());
    Assert.assertNotNull(locationJson.getGeometry());
    Assert.assertEquals(3879, locationJson.getGeometry().getSRID());
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
    Assert.assertNotNull(locationJson);
    Assert.assertNotNull(locationJson.getId());
    Assert.assertEquals(102, locationJson.getId().intValue());
    Assert.assertNotNull(locationJson.getPostalAddress());
    Assert.assertEquals("city, Json", locationJson.getPostalAddress().getCity());
    Assert.assertEquals("33333, Json", locationJson.getPostalAddress().getPostalCode());
    Assert.assertEquals("address, Json", locationJson.getPostalAddress().getStreetAddress());
    Assert.assertNotNull(locationJson.getGeometry());
    Assert.assertEquals(3879, locationJson.getGeometry().getSRID());
  }

  @Test
  public void updateLocationWithoutId() {
    LocationJson locationJson = MockServices.createLocationJson(null);
    locationJson = locationService.createLocations(APPLICATION_ID, Collections.singletonList(locationJson)).get(0);
    Assert.assertNotNull(locationJson);
    Assert.assertNotNull(locationJson.getId());
    Assert.assertEquals(102, locationJson.getId().intValue());
    Assert.assertNotNull(locationJson.getPostalAddress());
    Assert.assertEquals("city, Json", locationJson.getPostalAddress().getCity());
    Assert.assertEquals("33333, Json", locationJson.getPostalAddress().getPostalCode());
    Assert.assertEquals("address, Json", locationJson.getPostalAddress().getStreetAddress());
    Assert.assertNotNull(locationJson.getGeometry());
    Assert.assertEquals(3879, locationJson.getGeometry().getSRID());
  }

  @Test
  public void testFindById() {
    LocationJson locationJson = locationService.findLocationById(102);
    Assert.assertNotNull(locationJson);
    Assert.assertNotNull(locationJson.getId());
    Assert.assertEquals(102, locationJson.getId().intValue());
    Assert.assertNotNull(locationJson.getPostalAddress());
    Assert.assertEquals("City1, Model", locationJson.getPostalAddress().getCity());
    Assert.assertEquals("33333, Model", locationJson.getPostalAddress().getPostalCode());
    Assert.assertEquals("Street 1, Model", locationJson.getPostalAddress().getStreetAddress());
    Assert.assertNotNull(locationJson.getGeometry());
    Assert.assertEquals(3879, locationJson.getGeometry().getSRID());
  }

  @Test
  public void testGetFixedLocationList() {
    Mockito.when(restTemplate.getForEntity(Mockito.any(URI.class), Mockito.eq(FixedLocation[].class)))
        .then(invocation -> createMockFixedLocationList());

    List<FixedLocationJson> fixedLocationList = locationService.getFixedLocationList();
    Assert.assertEquals(2, fixedLocationList.size());
    Assert.assertEquals("FixedLocation 0", fixedLocationList.get(0).getArea());
  }

  @Test
  public void testGetFixedLocationAreaList() {
    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(FixedLocationArea[].class)))
        .then(invocation -> createMockFixedLocationAreaList());

    List<FixedLocationAreaJson> areaList = locationService.getFixedLocationAreaList();
    Assert.assertEquals(2, areaList.size());
    Assert.assertEquals("FixedLocation 0", areaList.get(0).getName());
    Assert.assertEquals("Section 0", areaList.get(0).getSections().get(0).getName());
  }

  private ResponseEntity<Location> createMockLocationResponse(HttpEntity<Location> request) {
    return new ResponseEntity<>(createMockLocationModel(request == null ? null : request.getBody()), HttpStatus.OK);
  }

  private static Location createMockLocationModel(Location input) {
    if (input != null && input.getId() != null) {
      return input;
    }
    Location location = new Location();
    location.setPostalAddress(new PostalAddress("Street 1, Model", "33333, Model", "City1, Model"));
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
