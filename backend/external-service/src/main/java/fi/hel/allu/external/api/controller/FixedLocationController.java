package fi.hel.allu.external.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.external.domain.FixedLocationExt;
import fi.hel.allu.external.mapper.FixedLocationMapper;
import fi.hel.allu.servicecore.service.LocationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiKeyAuthDefinition;
import io.swagger.annotations.ApiKeyAuthDefinition.ApiKeyLocation;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/v1/fixedlocations")
@Api(value = "v1/fixedlocations")
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
}
