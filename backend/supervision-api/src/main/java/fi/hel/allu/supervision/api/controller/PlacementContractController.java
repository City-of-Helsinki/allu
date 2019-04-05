package fi.hel.allu.supervision.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.ContractService;
import fi.hel.allu.supervision.api.domain.PlacementContractApplication;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1/placementcontracts")
@Api(tags = "Applications")
public class PlacementContractController extends BaseApplicationDetailsController<PlacementContractApplication> {

  @Autowired
  private ContractService contractService;

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.PLACEMENT_CONTRACT;
  }

  @Override
  protected PlacementContractApplication mapApplication(ApplicationJson application) {
    return new PlacementContractApplication(application);
  }

  @ApiOperation(value = "Gets contract for application with given ID. Returns contract draft / proposal if contract is not yet made",
      authorizations = @Authorization(value ="api_key"),
      response = byte.class,
      responseContainer = "Array")
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Contract retrieved successfully", response = byte.class, responseContainer = "Array"),
      @ApiResponse(code = 404, message = "No contract found for given application", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}/contract", method = RequestMethod.GET, produces = {"application/pdf", "application/json"})
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<byte[]> getPlacementContract(@PathVariable Integer id) {
    validateType(id);
    if (contractService.hasContract(id)) {
      return pdfResult(contractService.getContract(id));
    } else {
      return pdfResult(contractService.getContractPreview(id));
    }
  }
}
