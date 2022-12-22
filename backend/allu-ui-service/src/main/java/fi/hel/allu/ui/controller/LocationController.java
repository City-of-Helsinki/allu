package fi.hel.allu.ui.controller;

import fi.hel.allu.model.domain.FixedLocationArea;
import fi.hel.allu.servicecore.domain.CityDistrictInfoJson;
import fi.hel.allu.servicecore.domain.FixedLocationJson;
import fi.hel.allu.servicecore.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

  @GetMapping(value = "/fixed-location")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<FixedLocationJson>> getFixedLocationList() {
    return new ResponseEntity<>(locationService.getFixedLocationList(), HttpStatus.OK);
  }

  @GetMapping(value = "/city-district")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<CityDistrictInfoJson>> getCityDistrictList() {
    return new ResponseEntity<>(locationService.getCityDistrictList(), HttpStatus.OK);
  }

  @GetMapping(value = "/fixed-location-areas")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<FixedLocationArea>> getFixedLocationAreas() {
    return ResponseEntity.ok(locationService.getFixedLocationAreas());
  }
}