package fi.hel.allu.external.api.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.external.domain.PlacementContractExt;
import fi.hel.allu.external.service.ApplicationServiceExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/v1/placementcontracts")
@Api(value = "v1/placementcontracts")
public class PlacementContractController {

  @Autowired
  private ApplicationServiceExt applicationService;


  @ApiOperation(value = "Create new placement contract application.",
      produces = "application/json",
      response = Integer.class,
      authorizations=@Authorization(value ="api_key"))
  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Integer> create(@Valid @RequestBody PlacementContractExt placementContract) {
    return new ResponseEntity<>(applicationService.createPlacementContract(placementContract), HttpStatus.OK);
  }


}
