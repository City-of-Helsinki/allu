package fi.hel.allu.external.controller.api;

import fi.hel.allu.external.domain.InformationRequestExt;
import fi.hel.allu.external.domain.InformationRequestFieldExt;
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.servicecore.domain.InformationRequestFieldJson;
import fi.hel.allu.servicecore.domain.InformationRequestJson;
import fi.hel.allu.servicecore.service.InformationRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping({"/v1", "/v2"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Information requests", description = "API to read application information requests")
public class InformationRequestController {

    private final InformationRequestService informationRequestService;

    private final ApplicationServiceExt applicationService;

    public InformationRequestController(InformationRequestService informationRequestService,
                                        ApplicationServiceExt applicationService) {
        this.informationRequestService = informationRequestService;
        this.applicationService = applicationService;
    }

    @Operation(summary = "Fetch open information request for given application.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Information request retrieved successfully",
                    content = @Content(schema = @Schema(implementation = InformationRequestExt.class))),
            @ApiResponse(responseCode = "404", description = "No information request found for given application",
                    content = @Content)
    })
    @GetMapping(value = "/applications/{id}/informationrequests", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
    public ResponseEntity<InformationRequestExt> findByApplicationId(
            @Parameter(description = "Application ID to get information request for") @PathVariable Integer id) {
        Integer applicationId = applicationService.getApplicationIdForExternalId(id);
        applicationService.validateOwnedByExternalUser(applicationId);
        InformationRequestJson request = informationRequestService.findOpenByApplicationId(applicationId);
        return new ResponseEntity<>(toInformationRequestExt(request, id), HttpStatus.OK);
    }

    private InformationRequestExt toInformationRequestExt(InformationRequestJson request,
                                                          Integer externalApplicationId) {
        if (request == null) {
            return null;
        }
        return new InformationRequestExt(request.getId(), externalApplicationId,
                                         toInformationRequestExtFields(request.getFields()));
    }

    private List<InformationRequestFieldExt> toInformationRequestExtFields(List<InformationRequestFieldJson> fields) {
        return fields.stream().map(f -> new InformationRequestFieldExt(f.getFieldKey(), f.getDescription()))
                .collect(Collectors.toList());
    }

}