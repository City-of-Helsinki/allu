package fi.hel.allu.ui.service;

import fi.hel.allu.ui.domain.LocationJson;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class LocationServiceTest extends MockServices {
  @InjectMocks
  protected LocationService locationService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    initMocks();
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
    locationService.updateLocation(locationJson);
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
    locationService.updateLocation(locationJson);
    assertNotNull(locationJson);
    assertNull(locationJson.getId());
    assertNotNull(locationJson.getPostalAddress());
    assertEquals("city, Json", locationJson.getPostalAddress().getCity());
    assertEquals("33333, Json", locationJson.getPostalAddress().getPostalCode());
    assertEquals("address, Json", locationJson.getPostalAddress().getStreetAddress());
    assertNotNull(locationJson.getGeometry());
    assertEquals(3879, locationJson.getGeometry().getSRID());
  }

}
