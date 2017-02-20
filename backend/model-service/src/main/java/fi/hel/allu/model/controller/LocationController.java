package fi.hel.allu.model.controller;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.domain.CityDistrict;
import fi.hel.allu.model.domain.CityDistrictInfo;
import fi.hel.allu.model.domain.FixedLocation;
import fi.hel.allu.model.domain.Location;

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
   * @return  List of locations of application. Never <code>null</code>.
   */
  @RequestMapping(value = "/applications/{applicationId}", method = RequestMethod.GET)
  public ResponseEntity<List<Location>> findByApplication(@PathVariable int applicationId) {
    List<Location> locations = locationDao.findByApplication(applicationId);
    return new ResponseEntity<>(locations, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Location> update(@PathVariable int id, @Valid @RequestBody(required = true) Location location) {
    return new ResponseEntity<>(locationService.update(id, location), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Location> insert(@Valid @RequestBody(required = true) Location location) {
    if (location.getId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    return new ResponseEntity<>(locationService.insert(location), HttpStatus.OK);
  }

  /**
   * Delete a locations of given application.
   *
   * @param applicationId   id of the application.
   */
  @RequestMapping(value = "/applications/{applicationId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteLocation(@PathVariable int applicationId) {
    locationDao.deleteByApplication(applicationId);
    return new ResponseEntity<>(HttpStatus.OK);
  }


  @RequestMapping(value = "/fixed-location", method = RequestMethod.GET)
  public ResponseEntity<List<FixedLocation>> getFixedLocationList() {
    return new ResponseEntity<>(locationDao.getFixedLocationList(), HttpStatus.OK);
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
