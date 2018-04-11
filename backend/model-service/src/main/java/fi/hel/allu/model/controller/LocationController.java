package fi.hel.allu.model.controller;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.service.LocationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/locations")
public class LocationController {

  @Autowired
  private LocationDao locationDao;
  @Autowired
  private LocationService locationService;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Location> find(@PathVariable int id) {
    Optional<Location> location = locationDao.findById(id);
    Location locationValue = location
        .orElseThrow(() -> new NoSuchEntityException("Location not found", Integer.toString(id)));
    return new ResponseEntity<>(locationValue, HttpStatus.OK);
  }

  /**
   * Finds locations of given application.
   *
   * @param   applicationId   Id of the application whose locations should be returned.
   * @param   srid  SRID of geometry (spatial reference system)
   * @return  List of locations of application. Never <code>null</code>.
   */
  @RequestMapping(value = "/application/{applicationId}", method = RequestMethod.GET)
  public ResponseEntity<List<Location>> findByApplication(@PathVariable int applicationId, @RequestParam(value="srid", required=false) Integer srid) {
    List<Location> locations;
    if (srid != null) {
      locations = locationDao.findByApplication(applicationId, srid);
    } else {
      locations = locationDao.findByApplication(applicationId);
    }
    return new ResponseEntity<>(locations, HttpStatus.OK);
  }

  /**
   * Updates locations for the given application.
   *
   * @param applicationId Id of the application whose locations should be
   *          updated.
   * @param locations Locations to be updated.
   * @return Updated locations.
   */
  @RequestMapping(value = "/application/{applicationId}", method = RequestMethod.PUT)
  public ResponseEntity<List<Location>> updateApplicationLocations(@PathVariable int applicationId,
      @Valid @RequestBody List<Location> locations) {
    return new ResponseEntity<>(locationService.updateApplicationLocations(applicationId, locations), HttpStatus.OK);
  }

  /**
   * Adds new locations. All added locations must have the same application.
   *
   * @param   locations   Locations to be added.
   * @return  Added locations.
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<List<Location>> insert(@Valid @RequestBody List<Location> locations) {
    return new ResponseEntity<>(locationService.insert(locations), HttpStatus.OK);
  }

  /**
   * Delete a locations of given application.
   *
   * @param applicationId   id of the application.
   */
  // TODO: remove when locations are removed from the application class
  @RequestMapping(value = "/application/{applicationId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteLocation(@PathVariable int applicationId) {
    locationDao.deleteByApplication(applicationId);
    // TODO: if this method is not removed completely, change it to make required updates to application and project too
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Deletes given locations. All deleted locations must have the same application.
   *
   * @param   locations Locations to be deleted.
   * @return  Void.
   */
  @RequestMapping(value = "/delete", method = RequestMethod.POST)
  public ResponseEntity<Void> deleteLocations(@RequestBody List<Integer> locations) {
    locationService.delete(locations);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/fixed-location", method = RequestMethod.GET)
  public ResponseEntity<List<FixedLocation>> getFixedLocationList() {
    return new ResponseEntity<>(locationDao.getFixedLocationList(), HttpStatus.OK);
  }

  /**
   * Get all fixed location areas
   *
   * @param   srid  SRID of geometry (spatial reference system)
   * @return  List of fixed location areas.
   */

  @RequestMapping(value = "/fixed-location-areas", method = RequestMethod.GET)
  public ResponseEntity<List<FixedLocationArea>> getFixedLocationAreas(@RequestParam(value="srid", required=false) Integer srid) {
    return new ResponseEntity<>(locationDao.getFixedLocationAreas(srid), HttpStatus.OK);
  }

  /**
   * Get the list of known city districts.
   *
   * @return city district list
   */
  @RequestMapping(value = "/city-district", method = RequestMethod.GET)
  public ResponseEntity<List<CityDistrictInfo>> getCityDistrictList() {
    List<CityDistrictInfo> result = locationDao.getCityDistrictList().stream().map(LocationController::mapToInfo)
        .collect(Collectors.toList());
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // Make a stripped-down view of a city district: only the district ID + name.
  private static CityDistrictInfo mapToInfo(CityDistrict cityDistrict) {
    CityDistrictInfo result = new CityDistrictInfo();
    result.setId(cityDistrict.getId());
    result.setDistrictId(cityDistrict.getDistrictId());
    result.setName(cityDistrict.getName());
    return result;
  }
}
