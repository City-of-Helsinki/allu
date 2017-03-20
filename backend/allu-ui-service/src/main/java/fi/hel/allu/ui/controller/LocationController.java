package fi.hel.allu.ui.controller;

import fi.hel.allu.ui.domain.CityDistrictInfoJson;
import fi.hel.allu.ui.domain.FixedLocationAreaJson;
import fi.hel.allu.ui.domain.FixedLocationJson;
import fi.hel.allu.ui.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

  private LocationService locationService;

  @Autowired
  public LocationController(LocationService locationService) {
    this.locationService = locationService;
  }

  @RequestMapping(value = "/fixed-location", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<FixedLocationJson>> getFixedLocationList() {
    return new ResponseEntity<>(locationService.getFixedLocationList(), HttpStatus.OK);
  }

  @RequestMapping(value = "/fixed-location-areas", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<FixedLocationAreaJson>> getFixedLocationAreaList() {
    return new ResponseEntity<>(locationService.getFixedLocationAreaList(), HttpStatus.OK);
  }

  @RequestMapping(value = "/city-district", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<CityDistrictInfoJson>> getCityDistrictList() {
    return new ResponseEntity<>(locationService.getCityDistrictList(), HttpStatus.OK);
  }

}
