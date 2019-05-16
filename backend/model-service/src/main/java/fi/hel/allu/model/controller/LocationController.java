package fi.hel.allu.model.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.GeometryWrapper;
import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.model.service.LocationService;

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
      @RequestParam(required = true) int userId, @Valid @RequestBody List<Location> locations) {
    return new ResponseEntity<>(locationService.updateApplicationLocations(applicationId, locations, userId), HttpStatus.OK);
  }

  /**
   * Adds new locations. All added locations must have the same application.
   *
   * @param   locations   Locations to be added.
   * @return  Added locations.
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<List<Location>> insert(@RequestParam(required = true) int userId,
      @Valid @RequestBody List<Location> locations) {
    return new ResponseEntity<>(locationService.insert(locations, userId), HttpStatus.OK);
  }

  @RequestMapping(value = "/fixed-location", method = RequestMethod.GET)
  public ResponseEntity<List<FixedLocation>> getActiveLocations(@RequestParam(value = "applicationkind", required=false) ApplicationKind applicationKind,
                                                                @RequestParam(value="srid", required=false) Integer srId) {
    return new ResponseEntity<>(locationDao.getActiveFixedLocations(applicationKind, srId), HttpStatus.OK);
  }

  @RequestMapping(value = "/fixed-location/all", method = RequestMethod.GET)
  public ResponseEntity<List<FixedLocation>> getAllFixedLocations(@RequestParam(value = "applicationkind", required=false) ApplicationKind applicationKind,
                                                                  @RequestParam(value="srid", required=false) Integer srId) {
    return new ResponseEntity<>(locationDao.getAllFixedLocation(applicationKind, srId), HttpStatus.OK);
  }

  @RequestMapping(value = "/fixed-location/{id}", method = RequestMethod.GET)
  public ResponseEntity<FixedLocation> getFixedLocationById(@PathVariable Integer id, @RequestParam(value="srid", required=false) Integer srId) {
    return ResponseEntity.ok(locationDao.findFixedLocation(id, srId));
  }

  @RequestMapping(value = "/fixed-location-areas", method = RequestMethod.GET)
  public ResponseEntity<List<FixedLocationArea>> getFixedLocationAreas() {
    return ResponseEntity.ok(locationDao.getFixedLocationAreas());
  }

  /**
   * Get the list of known city districts.
   *
   * @return city district list
   */
  @RequestMapping(value = "/city-districts", method = RequestMethod.GET)
  public ResponseEntity<List<CityDistrictInfo>> getCityDistrictList() {
    List<CityDistrictInfo> result = locationDao.getCityDistrictList().stream().map(LocationController::mapToInfo)
        .collect(Collectors.toList());
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @RequestMapping(value = "/city-districts/{id}/name", method = RequestMethod.GET)
  public ResponseEntity<String> getCityDistrictNameId(@PathVariable Integer id) {
    String name = locationDao.getCityDistrictNameById(id);
    return ResponseEntity.ok(name);
  }

  @RequestMapping(value = "/city-districts/{id}", method = RequestMethod.GET)
  public ResponseEntity<CityDistrictInfo> getCityDistrictById(@PathVariable Integer id) {
    CityDistrictInfo cityDistrict = locationDao.getCityDistrictById(id)
        .map(c -> mapToInfo(c))
        .orElseThrow(() -> new NoSuchEntityException("No city district found for ID " + id));
    return ResponseEntity.ok(cityDistrict);
  }

  @RequestMapping(value = "/city-districts/{cityDistrictId}/supervisor/{type}", method = RequestMethod.GET)
  public ResponseEntity<User> findSupervisionTaskOwner(@PathVariable int cityDistrictId, @PathVariable ApplicationType type) {
    final Optional<User> optUser = locationService.findSupervisionTaskOwner(type, cityDistrictId);
    return optUser
        .map(user -> ResponseEntity.ok(user))
        .orElseThrow(() -> new NoSuchEntityException("Didn't find supervisor for citydistrict=" + cityDistrictId + " and applicationType=" + type));
  }

  @RequestMapping(value = "/geometry/isvalid", method = RequestMethod.POST)
  public ResponseEntity<Boolean> hasValidGeometry(@RequestBody GeometryWrapper geometryWrapper) {
    return new ResponseEntity<>(locationDao.isValidGeometry(geometryWrapper.getGeometry()), HttpStatus.OK);
  }

  @RequestMapping(value = "/geometry/transform", method = RequestMethod.POST)
  public ResponseEntity<GeometryWrapper> transfomGeometry(@RequestBody GeometryWrapper geometryWrapper, @RequestParam(value="srid") Integer srId) {
    return ResponseEntity.ok(new GeometryWrapper(locationDao.transformCoordinates(geometryWrapper.getGeometry(), srId)));
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
