package fi.hel.allu.supervision.api.service;

import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.servicecore.service.ChargeBasisService;
import fi.hel.allu.servicecore.service.LocationService;
import org.geolatte.geom.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationUpdateServiceTest {

  private static final Integer LOCATION_ID = 1;

  @Mock
  private LocationService locationService;
  @Mock
  private ChargeBasisService chargeBasisService;
  private LocationUpdateService locationUpdateService;
  private Location location;

  @BeforeEach
  public void setup() {
    location = new Location();
    ChargeBasisEntry[] chargeBasisEntries = new ChargeBasisEntry[2];
    locationUpdateService = new LocationUpdateService(locationService, chargeBasisService);
    when(locationService.getLocationById(LOCATION_ID)).thenReturn(location);
    when(locationService.updateLocation(any())).thenReturn(location);
  }

  @Test
  public void shouldUpdateLocationFields() {
    Map<String, Object> values = new HashMap<>();
    values.put("additionalInfo", "some info");
    locationUpdateService.update(LOCATION_ID, values);
    assertEquals("some info", location.getAdditionalInfo());
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

  @Test
  public void shouldUpdateChargeBasisOnUnderpassChange() {
    Map<String, Object> values = new HashMap<>();
    values.put("underpass", true);
    locationUpdateService.update(LOCATION_ID, values);

    assertEquals(values.get("underpass"), location.getUnderpass());
    verify(chargeBasisService).recalculateEntries(any());
  }

  @Test
  public void shouldNotUpdateChargeBasisOnUnderpassNotChanged() {
    Map<String, Object> values = new HashMap<>();
    values.put("additionalInfo", "some info");
    locationUpdateService.update(LOCATION_ID, values);
    assertEquals(values.get("additionalInfo"), location.getAdditionalInfo());
    verify(chargeBasisService, times(0)).recalculateEntries(any());
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