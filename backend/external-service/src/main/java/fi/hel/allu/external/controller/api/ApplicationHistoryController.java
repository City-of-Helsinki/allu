package fi.hel.allu.external.controller.api;

import fi.hel.allu.external.domain.ApplicationHistoryExt;
import fi.hel.allu.external.domain.ApplicationHistorySearchExt;
import fi.hel.allu.external.service.ApplicationServiceExt;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/v1/applicationhistory", "/v2/applicationhistory"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Application history")
public class ApplicationHistoryController {

    private final ApplicationServiceExt applicationService;

    public ApplicationHistoryController(ApplicationServiceExt applicationService) {
        this.applicationService = applicationService;
    }

    @Operation(summary = "Get Allu application history. " +
            "Returns result containing application status changes and supervision events.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data retrieved",
                    content = @Content(schema = @Schema(implementation = ApplicationHistoryExt.class))),
            @ApiResponse(responseCode = "404", description = "Data not found", content = @Content)
    })
    @PostMapping(produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
    public ResponseEntity<List<ApplicationHistoryExt>> searchApplicationHistory(
            @Parameter(
                    description = "Application history search parameters.") @RequestBody
            ApplicationHistorySearchExt searchParameters) {
        return new ResponseEntity<>(applicationService.searchApplicationHistory(searchParameters), HttpStatus.OK);
    }


}
