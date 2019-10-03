package fi.hel.allu.external.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.external.config.ApplicationProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/v1/applicationkinds")
@Api(value = "v1/applicationkinds")
public class ApplicationKindController {

  @Autowired
  private ApplicationProperties applicationProperties;

  @ApiOperation(value = "Get Allu application kinds",
      produces = "application/json",
      response = ApplicationKind.class,
      responseContainer="List",
      authorizations=@Authorization(value ="api_key"))
  @RequestMapping(method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<List<ApplicationKind>> getAll(@ApiParam(value = "Application type of the kinds to get", required = true)
                                                      @RequestParam(required = true) ApplicationType applicationType) {
    List<ApplicationKind> result = ApplicationKind.forApplicationType(applicationType)
        .stream()
        .filter(k -> !applicationProperties.getExcludedApplicationKinds().contains(k.name()))
        .collect(Collectors.toList());
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
