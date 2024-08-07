package fi.hel.allu.external.controller.api;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.external.domain.ShortTermRentalExt;
import fi.hel.allu.external.mapper.ShortTermRentalExtMapper;
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.external.validation.Validators;
import fi.hel.allu.servicecore.service.DecisionService;
import fi.hel.allu.servicecore.service.TerminationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/v1/shorttermrentals", "/v2/shorttermrentals"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Short term rentals", description = "Short term rental application API")
public class ShortTermRentalController extends BaseApplicationController<ShortTermRentalExt, ShortTermRentalExtMapper> {

    private final ShortTermRentalExtMapper shortTermRentalMapper;

    public ShortTermRentalController(ApplicationServiceExt applicationService,
                                     DecisionService decisionService,
                                     TerminationService terminationService,
                                     Validators validators,
                                     ShortTermRentalExtMapper shortTermRentalMapper) {
        super(applicationService, decisionService, validators, terminationService);
        this.shortTermRentalMapper = shortTermRentalMapper;
    }

    @Override
    protected void addApplicationTypeSpecificValidators(WebDataBinder binder) {
        binder.addValidators(validators.getShortTermRentalExtValidator());
    }


    @Override
    protected ShortTermRentalExtMapper getMapper() {
        return shortTermRentalMapper;
    }

    @Operation(summary = "Gets termination document for application with given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Termination document retrieved successfully",
                    content = @Content(schema = @Schema(implementation = byte.class))),
            @ApiResponse(responseCode = "404", description = "No termination document found for given application",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping(value = "/{id}/termination", produces = "application/pdf")
    @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
    public ResponseEntity<byte[]> getTermination(@PathVariable Integer id) {
        return getTerminationDocument(id);
    }
}