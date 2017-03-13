package fi.hel.allu.ui.controller;

import fi.hel.allu.ui.domain.CityDistrictInfoJson;
import fi.hel.allu.ui.domain.FixedLocationJson;
import fi.hel.allu.ui.domain.LocationJson;
import fi.hel.allu.ui.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

  private LocationService locationService;

  @Autowired
  public LocationController(LocationService locationService) {
    this.locationService = locationService;
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<LocationJson> find(@PathVariable int id) {
    return new ResponseEntity<>(locationService.findLocationById(id), HttpStatus.OK);
  }

  /**
   * Finds locations of given application.
   *
   * @param   applicationId   Id of the application whose locations should be returned.
   * @return  List of locations of application. Never <code>null</code>.
   */
  @RequestMapping(value = "/application/{applicationId}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<LocationJson>> findByApplication(@PathVariable int applicationId) {
    return new ResponseEntity<>(locationService.findLocationsByApplication(applicationId), HttpStatus.OK);
  }

  /**
   * Updates given locations. All locations must have the same application.
   *
   * @param   locations   Locations to be updated.
   * @return  Updated locations.
   */
  @RequestMapping(value = "/application/{applicationId}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<LocationJson>> update(
      @PathVariable int applicationId,
      @Valid @RequestBody List<LocationJson> locations) {
    return new ResponseEntity<>(locationService.update(applicationId, locations), HttpStatus.OK);
  }

  /**
   * Adds new locations. All added locations must have the same application.
   *
   * @param   locations   Locations to be added.
   * @return  Added locations.
   */
  @RequestMapping(value = "/application/{applicationId}", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<LocationJson>> insert(
      @PathVariable int applicationId,
      @Valid @RequestBody List<LocationJson> locations) {
    return new ResponseEntity<>(locationService.insert(applicationId, locations), HttpStatus.OK);
  }

  /**
   * Deletes given locations. All deleted locations must have the same application.
   *
   * @param   locations Locations to be deleted.
   * @return  Void.
   */
  @RequestMapping(value = "/delete", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> deleteLocations(@RequestBody List<Integer> locations) {
    locationService.delete(locations);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/fixed-location", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<FixedLocationJson>> getFixedLocationList() {
    return new ResponseEntity<>(locationService.getFixedLocationList(), HttpStatus.OK);
  }

  @RequestMapping(value = "/city-district", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<CityDistrictInfoJson>> getCityDistrictList() {
    return new ResponseEntity<>(locationService.getCityDistrictList(), HttpStatus.OK);
  }

}
