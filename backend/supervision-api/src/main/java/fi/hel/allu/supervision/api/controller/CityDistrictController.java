package fi.hel.allu.supervision.api.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.servicecore.domain.CityDistrictInfoJson;
import fi.hel.allu.servicecore.service.LocationService;

@RestController
@RequestMapping("/v1/citydistricts")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "City districts")
public class CityDistrictController {

  @Autowired
  private LocationService locationService;

  @Operation(summary = "List all city districts")
  @RequestMapping(method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
  public ResponseEntity<List<CityDistrictInfoJson>> getAllCityDistricts() {
    return ResponseEntity.ok(locationService.getCityDistrictList());
  }

  @Operation(summary = "Get city district by id")
  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
  public ResponseEntity<CityDistrictInfoJson> getCityDistrictById(@PathVariable Integer id) {
    return ResponseEntity.ok(locationService.getCityDistrictById(id));
  }
}
