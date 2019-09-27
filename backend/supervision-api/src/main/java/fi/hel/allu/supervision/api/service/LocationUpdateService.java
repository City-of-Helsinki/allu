package fi.hel.allu.supervision.api.service;

import fi.hel.allu.model.domain.Location;
import fi.hel.allu.servicecore.service.LocationService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LocationUpdateService extends ModelFieldUpdater {

  private LocationService locationService;

  public LocationUpdateService(LocationService locationService) {
    this.locationService = locationService;
  }

  public Location update(Integer id, Map<String, Object> fields) {
    Location location = locationService.getLocationById(id);
    if (location == null) {
      throw new IllegalArgumentException("The specified location does not exist");
    }
    updateObject(fields, location);
    return locationService.updateLocation(location);
  }

  @Override
  protected boolean requireUpdatablePropertyAnnotation() {
    return false;
  }

}
