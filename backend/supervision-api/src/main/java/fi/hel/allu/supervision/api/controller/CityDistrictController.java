package fi.hel.allu.supervision.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.servicecore.domain.CityDistrictInfoJson;
import fi.hel.allu.servicecore.service.LocationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/v1/citydistricts")
@Api(tags = "City districts")
public class CityDistrictController {

  @Autowired
  private LocationService locationService;

  @ApiOperation(value = "List all city districts",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<List<CityDistrictInfoJson>> getAllCityDistricts() {
    return ResponseEntity.ok(locationService.getCityDistrictList());
  }

  @ApiOperation(value = "Get city district by id",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<CityDistrictInfoJson> getCityDistrictById(@PathVariable Integer id) {
    return ResponseEntity.ok(locationService.getCityDistrictById(id));
  }
}
