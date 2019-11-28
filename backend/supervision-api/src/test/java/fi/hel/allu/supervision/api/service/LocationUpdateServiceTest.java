package fi.hel.allu.supervision.api.service;

import fi.hel.allu.model.domain.Location;
import fi.hel.allu.servicecore.service.LocationService;
import org.geolatte.geom.Point;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationUpdateServiceTest {

  private static final Integer LOCATION_ID = 1;

  @Mock
  private LocationService locationService;
  private LocationUpdateService locationUpdateService;
  private Location location;

  @Before
  public void setup() {
    location = new Location();
    locationUpdateService = new LocationUpdateService(locationService);
    when(locationService.getLocationById(LOCATION_ID)).thenReturn(location);
    when(locationService.updateLocation(any())).thenReturn(location);
  }

  @Test
  public void shouldUpdateLocationFields() {
    Map<String, Object> values = new HashMap<>();
    values.put("additionalInfo", "some info");
    locationUpdateService.update(LOCATION_ID, values);
    assertEquals(values.get("additionalInfo"), location.getAdditionalInfo());
  }

  @Test
  public void shouldUpdateLocationGeometry() {
    double lat = 60.192059;
    double lon = 24.945831;

    Map<String, Object> point = createPointGeometryMap(lat, lon);

    Map<String, Object> values = new HashMap<>();
    values.put("geometry", point);
    locationUpdateService.update(LOCATION_ID, values);

    Point updatedPoint = location.getGeometry().getPointN(0);
    assertEquals(lat, updatedPoint.getX(), 0.000001);
    assertEquals(lon, updatedPoint.getY(), 0.000001);
  }

  private Map<String, Object> createPointGeometryMap(double lat, double lon) {
    Map<String, Object> properties = new HashMap<>();
    properties.put("name", "EPSG:4326");

    Map<String, Object> crs = new HashMap<>();
    crs.put("properties", properties);
    crs.put("type", "name");

    Map<String, Object> point = new HashMap<>();
    point.put("type", "Point");
    point.put("crs", crs);
    point.put("bbox", new Double[] {lat, lon, lat, lon});
    point.put("coordinates", new Double[] {lat, lon});

    return point;
  }

}
