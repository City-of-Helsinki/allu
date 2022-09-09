package fi.hel.allu.supervision.api.controller;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.servicecore.domain.ApplicationTagJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/v1/applications")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Application tags")
public class ApplicationTagController {

    protected static final List<ApplicationTagType> ALLOWED_TAG_TYPES = Arrays.asList(
            ApplicationTagType.WAITING,
            ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED,
            ApplicationTagType.STATEMENT_REQUESTED,
            ApplicationTagType.COMPENSATION_CLARIFICATION,
            ApplicationTagType.PAYMENT_BASIS_CORRECTION,
            ApplicationTagType.SURVEY_REQUIRED,
            ApplicationTagType.OTHER_CHANGES,
            ApplicationTagType.DECISION_NOT_SENT,
            ApplicationTagType.CONTRACT_REJECTED
    );
    private final ApplicationServiceComposer applicationServiceComposer;

    public ApplicationTagController(ApplicationServiceComposer applicationServiceComposer) {
        this.applicationServiceComposer = applicationServiceComposer;
    }


    @Operation(
            summary = "Add new tag for an application with given ID. If application already has a tag with given type" +
                    " no new tag is added.",
            description = "User is allowed to add following tags:"
                    + "<ul>"
                    + " <li>WAITING</li>"
                    + " <li>ADDITIONAL_INFORMATION_REQUESTED</li>"
                    + " <li>STATEMENT_REQUESTED</li>"
                    + " <li>COMPENSATION_CLARIFICATION</li>"
                    + " <li>PAYMENT_BASIS_CORRECTION</li>"
                    + " <li>SURVEY_REQUIRED</li>"
                    + " <li>OTHER_CHANGES</li>"
                    + " <li>DECISION_NOT_SENT</li>"
                    + " <li>CONTRACT_REJECTED</li>"
                    + "</ul>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag added successfully",
                    content = @Content(schema = @Schema(implementation = ApplicationTagJson.class))),
            @ApiResponse(responseCode = "400", description = "Invalid tag type",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PostMapping(value = "/applications/{id}/tags", produces = "application/json",
            consumes = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<ApplicationTagJson> addTag(@PathVariable Integer id,
                                                     @RequestBody ApplicationTagType tagType) {
        validateTagType(tagType);
        return ResponseEntity.ok(applicationServiceComposer.addTag(id, new ApplicationTagJson(null, tagType, null)));
    }

    @Operation(summary = "Remove tag from an application with given ID.",
            description = "User is allowed to remove following tags:"
                    + "<ul>"
                    + " <li>WAITING</li>"
                    + " <li>ADDITIONAL_INFORMATION_REQUESTED</li>"
                    + " <li>STATEMENT_REQUESTED</li>"
                    + " <li>COMPENSATION_CLARIFICATION</li>"
                    + " <li>PAYMENT_BASIS_CORRECTION</li>"
                    + " <li>SURVEY_REQUIRED</li>"
                    + "</ul>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag removed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid tag type",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @DeleteMapping(value = "/applications/{id}/tags/{tagType}",
            produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<Void> deleteTag(@PathVariable Integer id, @PathVariable ApplicationTagType tagType) {
        validateTagType(tagType);
        applicationServiceComposer.removeTag(id, tagType);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void validateTagType(ApplicationTagType tagType) {
        if (!ALLOWED_TAG_TYPES.contains(tagType)) {
            throw new IllegalArgumentException("applicationTag.type.invalid");
        }
    }
}
