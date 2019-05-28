package fi.hel.allu.external.maintenance.controller;

import fi.hel.allu.servicecore.service.CityDistrictUpdaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public interface for managing city districts.
 */
@RestController
@RequestMapping("/v1/citydistricts")
public class CityDistrictController {

  @Autowired
  CityDistrictUpdaterService updaterService;

  @RequestMapping(method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_SERVICE')")
  public ResponseEntity<Void> update() {
    updaterService.update();
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
