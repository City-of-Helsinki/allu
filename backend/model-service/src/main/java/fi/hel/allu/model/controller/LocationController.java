package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.GeometryWrapper;
import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.model.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
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

  @GetMapping(value = "/{id}")
  public ResponseEntity<Location> find(@PathVariable int id) {
    Optional<Location> location = locationDao.findById(id);
    Location locationValue = location
        .orElseThrow(() -> new NoSuchEntityException("Location not found", Integer.toString(id)));
    return new ResponseEntity<>(locationValue, HttpStatus.OK);
  }

  @PostMapping(value = "/multi")
  public ResponseEntity<List<Location>> findMultiple(@RequestBody List<Integer> ids) {
    List<Location> locations = locationDao.findByIds(ids);
    return ResponseEntity.ok(locations);
  }

  /**
   * Finds locations of given application.
   *
   * @param   applicationId   Id of the application whose locations should be returned.
   * @param   srid  SRID of geometry (spatial reference system)
   * @return  List of locations of application. Never <code>null</code>.
   */
  @GetMapping(value = "/application/{applicationId}")
  public ResponseEntity<List<Location>> findByApplication(@PathVariable int applicationId, @RequestParam(value="srid", required=false) Integer srid) {
    List<Location> locations;
    if (srid != null) {
      locations = locationDao.findByApplicationId(applicationId, srid);
    } else {
      locations = locationDao.findByApplicationId(applicationId);
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
  @PutMapping(value = "/application/{applicationId}")
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
  @PostMapping
  public ResponseEntity<List<Location>> insert(@RequestParam(required = true) int userId,
      @Valid @RequestBody List<Location> locations) {
    return new ResponseEntity<>(locationService.insert(locations, userId), HttpStatus.OK);
  }

  /**
   * Deletes an existing location.
   *
   * @param   locationId   Location id to be deleted.
   */
  @DeleteMapping(value = "/{locationId}")
  public ResponseEntity<Void> delete(@RequestParam int userId,
                                     @PathVariable int locationId) {
    locationService.delete(Collections.singletonList(locationId), userId);
    return ResponseEntity.ok().build();
  }

  /**
   * Update existing location.
   *
   * @param   location Location to update.
   * @return  Updated location.
   */
  @PutMapping(value = "/{locationId}")
  public ResponseEntity<Location> update(@RequestParam int userId,
                                         @PathVariable int locationId,
                                         @Valid @RequestBody Location location) {
    location.setId(locationId);
    List<Location> updated = locationService.update(Collections.singletonList(location), userId);
    return ResponseEntity.ok(updated.stream().findFirst().orElse(null));
  }

  @GetMapping(value = "/fixed-location")
  public ResponseEntity<List<FixedLocation>> getActiveLocations(@RequestParam(value = "applicationkind", required=false) ApplicationKind applicationKind,
                                                                @RequestParam(value="srid", required=false) Integer srId) {
    return new ResponseEntity<>(locationDao.getActiveFixedLocations(applicationKind, srId), HttpStatus.OK);
  }

  @GetMapping(value = "/fixed-location/all")
  public ResponseEntity<List<FixedLocation>> getAllFixedLocations(@RequestParam(value = "applicationkind", required=false) ApplicationKind applicationKind,
                                                                  @RequestParam(value="srid", required=false) Integer srId) {
    return new ResponseEntity<>(locationDao.getAllFixedLocation(applicationKind, srId), HttpStatus.OK);
  }

  @GetMapping(value = "/fixed-location/{id}")
  public ResponseEntity<FixedLocation> getFixedLocationById(@PathVariable Integer id, @RequestParam(value="srid", required=false) Integer srId) {
    return ResponseEntity.ok(locationDao.findFixedLocation(id, srId));
  }

  @GetMapping(value = "/fixed-location-areas")
  public ResponseEntity<List<FixedLocationArea>> getFixedLocationAreas() {
    return ResponseEntity.ok(locationDao.getFixedLocationAreas());
  }

  /**
   * Get the list of known city districts.
   *
   * @return city district list
   */
  @GetMapping(value = "/city-districts")
  public ResponseEntity<List<CityDistrictInfo>> getCityDistrictList() {
    List<CityDistrictInfo> result = locationDao.getCityDistrictList().stream().map(LocationController::mapToInfo)
        .collect(Collectors.toList());
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @GetMapping(value = "/city-districts/{id}/name")
  public ResponseEntity<String> getCityDistrictNameId(@PathVariable Integer id) {
    String name = locationDao.getCityDistrictNameById(id);
    return ResponseEntity.ok(name);
  }

  @GetMapping(value = "/city-districts/{id}")
  public ResponseEntity<CityDistrictInfo> getCityDistrictById(@PathVariable Integer id) {
    CityDistrictInfo cityDistrict = locationDao.getCityDistrictById(id)
        .map(c -> mapToInfo(c))
        .orElseThrow(() -> new NoSuchEntityException("No city district found for ID " + id));
    return ResponseEntity.ok(cityDistrict);
  }

  @GetMapping(value = "/city-districts/{cityDistrictId}/supervisor/{type}")
  public ResponseEntity<User> findSupervisionTaskOwner(@PathVariable int cityDistrictId, @PathVariable ApplicationType type) {
    if (!type.equals(ApplicationType.CABLE_REPORT)){
    final Optional<User> optUser = locationService.findSupervisionTaskOwner(type, cityDistrictId);
      return optUser
        .map(user -> ResponseEntity.ok(user))
        .orElseThrow(() -> new NoSuchEntityException("Didn't find supervisor for citydistrict=" + cityDistrictId + " and applicationType=" + type));

    }
    else {
      return ResponseEntity.ok(new User());
    }
  }

  @PostMapping(value = "/geometry/isvalid")
  public ResponseEntity<Boolean> hasValidGeometry(@RequestBody GeometryWrapper geometryWrapper) {
    return new ResponseEntity<>(locationDao.isValidGeometry(geometryWrapper.getGeometry()), HttpStatus.OK);
  }

  @PostMapping(value = "/geometry/transform")
  public ResponseEntity<GeometryWrapper> transformGeometry(@RequestBody GeometryWrapper geometryWrapper, @RequestParam(value="srid") Integer srId) {
    return ResponseEntity.ok(new GeometryWrapper(locationDao.transformCoordinates(geometryWrapper.getGeometry(), srId)));
  }

  @PostMapping(value = "/geometry/simplify")
  public ResponseEntity<GeometryWrapper> simplifyGeometry(@RequestBody GeometryWrapper geometryWrapper, @RequestParam(value="zoom") Integer zoomLevel) {
    return ResponseEntity.ok(new GeometryWrapper(locationService.simplifyGeometry(geometryWrapper.getGeometry(), zoomLevel)));
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