package fi.hel.allu.external.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.external.domain.ApplicationHistoryExt;
import fi.hel.allu.external.domain.ApplicationHistorySearchExt;
import fi.hel.allu.external.service.ApplicationServiceExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/v1/applicationhistory")
@Api(value = "v1/applicationhistory")
public class ApplicationHistoryController {

  @Autowired
  private ApplicationServiceExt applicationService;

  @ApiOperation(value = "Get Allu application history. Returns result containing application status changes and supervision events.",
      produces = "application/json",
      response = ApplicationHistoryExt.class,
      responseContainer="List",
      authorizations=@Authorization(value ="api_key"))
  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<List<ApplicationHistoryExt>> searchApplicationHistory(@ApiParam(value = "Application history search parameters.") @RequestBody ApplicationHistorySearchExt searchParameters) {
    return new ResponseEntity<>(applicationService.searchApplicationHistory(searchParameters), HttpStatus.OK);
  }


}
