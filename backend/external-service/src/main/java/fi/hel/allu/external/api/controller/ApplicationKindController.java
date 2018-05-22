package fi.hel.allu.external.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/v1/applicationkinds")
@Api(value = "v1/applicationkinds")
public class ApplicationKindController {

  @ApiOperation(value = "Get Allu application kinds",
      produces = "application/json",
      response = ApplicationKind.class,
      responseContainer="List",
      authorizations=@Authorization(value ="api_key"))
  @RequestMapping(method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<List<ApplicationKind>> getAll(@ApiParam(value = "Application type of the kinds to get", required = true)
                                                      @RequestParam(required = true) ApplicationType applicationType) {
    return new ResponseEntity<>(ApplicationKind.forApplicationType(applicationType), HttpStatus.OK);
  }


}
