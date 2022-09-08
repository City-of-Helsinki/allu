package fi.hel.allu.external.controller.maintenance;

import fi.hel.allu.servicecore.service.CityDistrictUpdaterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public interface for managing city districts.
 */
@RestController
@RequestMapping("/v1/citydistricts")
@Tag(name = "City District", description = "Used only for maintenance")
public class CityDistrictController {

  CityDistrictUpdaterService updaterService;

  public CityDistrictController(CityDistrictUpdaterService updaterService) {
    this.updaterService = updaterService;
  }

  @PutMapping
  @PreAuthorize("hasAnyRole('ROLE_SERVICE')")
  public ResponseEntity<Void> update() {
    updaterService.update();
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
