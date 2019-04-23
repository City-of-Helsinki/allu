package fi.hel.allu.servicecore.service;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.model.domain.FixedLocation;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.FixedLocationJson;
import fi.hel.allu.servicecore.domain.LocationJson;

public class LocationServiceTest {
  private static Validator validator;
  protected LocationService locationService;
  protected RestTemplate restTemplate;

  @BeforeClass
  public static void setUpBeforeClass() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Before
  public void setUp() {
    restTemplate = Mockito.mock(RestTemplate.class);
    ApplicationProperties properties = Mockito.mock(ApplicationProperties.class);
    locationService = new LocationService(properties, restTemplate);
    Mockito.when(properties.getFixedLocationUrl()).thenReturn("http://fixedlocations");
  }

  @Test
  public void testValidationWithValidLocation() {
    Set<ConstraintViolation<LocationJson>> constraintViolations =
        validator.validate(MockServices.createLocationJson(1));
    Assert.assertEquals(0, constraintViolations.size());
  }

  @Test
  public void testGetFixedLocationList() {
    Mockito.when(restTemplate.getForEntity(Mockito.any(URI.class), Mockito.eq(FixedLocation[].class)))
        .then(invocation -> createMockFixedLocationList());

    List<FixedLocationJson> fixedLocationList = locationService.getFixedLocationList();
    Assert.assertEquals(2, fixedLocationList.size());
    Assert.assertEquals("FixedLocation 0", fixedLocationList.get(0).getArea());
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
}
