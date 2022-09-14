package fi.hel.allu.supervision.api.controller;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.CreateCustomerWithContactsJson;
import fi.hel.allu.servicecore.domain.CreatePlacementContractApplicationJson;
import fi.hel.allu.servicecore.domain.CustomerWithContactsJson;
import fi.hel.allu.servicecore.service.*;
import fi.hel.allu.supervision.api.domain.PlacementContractApplication;
import fi.hel.allu.supervision.api.mapper.ApplicationMapperCollector;
import fi.hel.allu.supervision.api.service.ApplicationUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/placementcontracts")
@Tag(name = "Applications")
public class PlacementContractController extends BaseApplicationDetailsController<PlacementContractApplication,
        CreatePlacementContractApplicationJson> {

    private final ContractService contractService;

    public PlacementContractController(ApprovalDocumentService approvalDocumentService,
                                       ChargeBasisService chargeBasisService,
                                       ApplicationServiceComposer applicationServiceComposer,
                                       ApplicationUpdateService applicationUpdateService,
                                       LocationService locationService,
                                       ApplicationMapperCollector applicationMapperCollector,
                                       ContractService contractService) {
        super(approvalDocumentService, chargeBasisService, applicationServiceComposer, applicationUpdateService,
              locationService, applicationMapperCollector);
        this.contractService = contractService;
    }

    @Override
    protected ApplicationType getApplicationType() {
        return ApplicationType.PLACEMENT_CONTRACT;
    }

    @Override
    protected PlacementContractApplication mapApplication(ApplicationJson application) {
        return new PlacementContractApplication(application);
    }

    @Operation(
            summary = "Gets contract for application with given ID. Returns contract draft / proposal if contract is " +
					"not yet" +
                    " made")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contract retrieved successfully",
                    content = @Content(schema = @Schema(implementation = byte.class))),
            @ApiResponse(responseCode = "404", description = "No contract found for given application",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping(value = "/{id}/contract", produces = {"application/pdf", "application/json"})
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<byte[]> getPlacementContract(@PathVariable Integer id) {
        validateType(id);
        if (contractService.hasContract(id)) {
            return pdfResult(contractService.getContract(id));
        } else {
            return pdfResult(contractService.getContractPreview(id));
        }
    }

    @Override
    @PutMapping(value = "/{applicationId}/applicant", produces = "application/json")
    public ResponseEntity<CustomerWithContactsJson> updateCustomerApplicant(@PathVariable Integer applicationId,
                                                                            @RequestBody @Parameter(
                                                                                    description = "The new customer " +
																							"with contacts")
                                                                            CreateCustomerWithContactsJson customer) {
        return super.updateCustomerApplicant(applicationId, customer);
    }

    @Override
    @PutMapping(value = "/{applicationId}/representative", produces = "application/json")
    public ResponseEntity<CustomerWithContactsJson> updateCustomerRepresentative(@PathVariable Integer applicationId,
                                                                                 @RequestBody @Parameter(
                                                                                         description = "The new customer with contacts")
                                                                                 CreateCustomerWithContactsJson customer) {
        return super.updateCustomerRepresentative(applicationId, customer);
    }

    @Override
    @DeleteMapping(value = "/{applicationId}/representative", produces = "application/json")
    public ResponseEntity<Void> removeRepresentative(@PathVariable Integer applicationId) {
        return super.removeRepresentative(applicationId);
    }

}
