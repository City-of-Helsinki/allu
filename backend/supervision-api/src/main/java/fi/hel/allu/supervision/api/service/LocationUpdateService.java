package fi.hel.allu.supervision.api.service;

import fi.hel.allu.model.domain.Location;
import fi.hel.allu.servicecore.service.ChargeBasisService;
import fi.hel.allu.servicecore.service.LocationService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LocationUpdateService extends ModelFieldUpdater {

  private LocationService locationService;
  private ChargeBasisService chargeBasisService;

  public LocationUpdateService(LocationService locationService, ChargeBasisService chargeBasisService) {
    this.locationService = locationService;
    this.chargeBasisService = chargeBasisService;
  }

  public Location update(Integer id, Map<String, Object> fields) {
    Location location = locationService.getLocationById(id);
    if (location == null) {
      throw new IllegalArgumentException("The specified location does not exist");
    }
    // Save underpass value of old location object for the check found later in this method
    Boolean oldUnderpass = location.getUnderpass();
    updateObject(fields, location);
    Location updatedLocation = locationService.updateLocation(location);
    // Update charge basis entries if underpass value was changed
    if (updatedLocation.getUnderpass() != null && !updatedLocation.getUnderpass().equals(oldUnderpass)) {
      chargeBasisService.recalculateEntries(updatedLocation.getApplicationId());
    }
    return updatedLocation;
  }

  @Override
  protected boolean requireUpdatablePropertyAnnotation() {
    return false;
  }

}
