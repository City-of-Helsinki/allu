package fi.hel.allu.ui.service;

import fi.hel.allu.ui.domain.FixedLocationJson;
import fi.hel.allu.ui.domain.LocationJson;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LocationServiceTest extends MockServices {
  private static Validator validator;
  @InjectMocks
  protected LocationService locationService;

  @BeforeClass
  public static void setUpBeforeClass() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    initSaveMocks();
    initSearchMocks();
  }

  @Test
  public void testValidationWithValidLocation() {
    Set<ConstraintViolation<LocationJson>> constraintViolations =
        validator.validate(createLocationJson(1));
    assertEquals(0, constraintViolations.size());
  }

  @Test
  public void createValidLocation() {
    LocationJson locationJson = locationService.createLocation(createLocationJson(null));
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
  public void createLocationWithId() {
    LocationJson locationJsonRequest = createLocationJson(1);
    LocationJson locationJson = locationService.createLocation(locationJsonRequest);
    assertNotNull(locationJson);
    assertNotNull(locationJson.getId());
    assertEquals(1, locationJson.getId().intValue());
    assertNotNull(locationJson.getPostalAddress());
    assertEquals("city, Json", locationJson.getPostalAddress().getCity());
    assertEquals("33333, Json", locationJson.getPostalAddress().getPostalCode());
    assertEquals("address, Json", locationJson.getPostalAddress().getStreetAddress());
    assertNotNull(locationJson.getGeometry());
    assertEquals(3879, locationJson.getGeometry().getSRID());
  }

  @Test
  public void updateValidLocation() {
    LocationJson locationJson = createLocationJson(1);
    locationJson = locationService.updateOrCreateLocation(locationJson);
    assertNotNull(locationJson);
    assertNotNull(locationJson.getId());
    assertEquals(1, locationJson.getId().intValue());
    assertNotNull(locationJson.getPostalAddress());
    assertEquals("city, Json", locationJson.getPostalAddress().getCity());
    assertEquals("33333, Json", locationJson.getPostalAddress().getPostalCode());
    assertEquals("address, Json", locationJson.getPostalAddress().getStreetAddress());
    assertNotNull(locationJson.getGeometry());
    assertEquals(3879, locationJson.getGeometry().getSRID());
  }

  @Test
  public void updateLocationWithoutId() {
    LocationJson locationJson = createLocationJson(null);
    locationJson = locationService.updateOrCreateLocation(locationJson);
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
    List<FixedLocationJson> fixedLocationList = locationService.getFixedLocationList();
    assertEquals(2, fixedLocationList.size());
    assertEquals("FixedLocation 0", fixedLocationList.get(0).getArea());
  }
}
