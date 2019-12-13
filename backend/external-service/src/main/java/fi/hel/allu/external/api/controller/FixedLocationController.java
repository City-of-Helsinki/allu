package fi.hel.allu.external.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.external.domain.FixedLocationExt;
import fi.hel.allu.external.mapper.FixedLocationMapper;
import fi.hel.allu.servicecore.service.LocationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping({"/v1/fixedlocations", "/v2/fixedlocations"})
@Api(tags = "Fixed locations")
public class FixedLocationController {

  @Autowired
  private LocationService locationService;

  @ApiOperation(value = "Get fixed locations",
      produces = "application/json",
      response = FixedLocationExt.class,
      responseContainer="List",
      authorizations=@Authorization(value ="api_key"))
  @RequestMapping(method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<List<FixedLocationExt>> getAll(@ApiParam(value = "Application kind of the fixed locations to get", required = true)
                                                       @RequestParam(required = true) ApplicationKind applicationKind,
                                                       @ApiParam(value = "Spatial reference system ID of the geometry.", required = false, defaultValue = "3879")
                                                       @RequestParam(required = false) Integer srId) {
    return new ResponseEntity<>(FixedLocationMapper.mapToExt(locationService.getFixedLocationList(applicationKind, srId)), HttpStatus.OK);
  }

  @ApiOperation(value = "Get fixed location by ID",
      produces = "application/json",
      response = FixedLocationExt.class,
      authorizations=@Authorization(value ="api_key"))
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<FixedLocationExt> findById(@ApiParam(value = "Id of the fixed location") @PathVariable Integer id,
                                                   @ApiParam(value = "Spatial reference system ID of the geometry.", required = false, defaultValue = "3879")
                                                   @RequestParam(required = false) Integer srId) {
    return new ResponseEntity<>(FixedLocationMapper.mapToExt(locationService.getFixedLocationById(id, srId)), HttpStatus.OK);
  }

}
