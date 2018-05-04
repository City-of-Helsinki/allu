package fi.hel.allu.external.api.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import fi.hel.allu.external.domain.PlacementContractExt;
import fi.hel.allu.external.service.ApplicationServiceExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/v1/placementcontracts")
@Api(value = "v1/placementcontracts")
public class PlacementContractController {

  @Autowired
  private ApplicationServiceExt applicationService;


  @ApiOperation(value = "Create new placement contract application. "
      + "If application is still pending in client side, pendingOnClient should be set to true to prevent handling "
      + "of application in Allu and to allow later modification of application data by client. If application is ready to be handled "
      + "in Allu, pendingOnClient should be set to false.",
      produces = "application/json",
      response = Integer.class,
      authorizations=@Authorization(value ="api_key"))
  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Integer> create(@ApiParam(value = "Placement contract data") @Valid @RequestBody PlacementContractExt placementContract) throws JsonProcessingException {
    return new ResponseEntity<>(applicationService.createPlacementContract(placementContract), HttpStatus.OK);
  }

  @ApiOperation(value = "Update placement contract application. Allowed only if application was created with pendingOnClient = true",
      produces = "application/json",
      response = Integer.class,
      authorizations=@Authorization(value ="api_key"))
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Integer> update(@ApiParam(value = "Id of the placement contract application to update.")
                                        @PathVariable Integer id,
                                        @ApiParam(value = "Placement contract data")
                                        @Valid @RequestBody PlacementContractExt placementContract) throws JsonProcessingException {
    applicationService.validateFullUpdateAllowed(id);
    applicationService.validateOwnedByExternalUser(id);
    return new ResponseEntity<>(applicationService.updatePlacementContract(id, placementContract), HttpStatus.OK);
  }


}
