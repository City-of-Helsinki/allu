package fi.hel.allu.supervision.api.controller;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.util.PdfMerger;
import fi.hel.allu.servicecore.domain.DecisionDetailsJson;
import fi.hel.allu.servicecore.domain.DecisionDocumentType;
import fi.hel.allu.servicecore.domain.DistributionEntryJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.AttachmentService;
import fi.hel.allu.servicecore.service.DecisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/applications")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Applications")
public class DecisionController {

    private final DecisionService decisionService;
    private final AttachmentService attachmentService;
    private final ApplicationServiceComposer applicationServiceComposer;

    public DecisionController(DecisionService decisionService, AttachmentService attachmentService,
                              ApplicationServiceComposer applicationServiceComposer) {
        this.decisionService = decisionService;
        this.attachmentService = attachmentService;
        this.applicationServiceComposer = applicationServiceComposer;
    }

    @Operation(
            summary = "Gets decision document for application with given ID. Returns draft if decision is not yet " +
					"made. "
                    + "Available for all application types except notes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Decision document retrieved successfully",
                    content = @Content(schema = @Schema(implementation = byte.class))),
            @ApiResponse(responseCode = "404", description = "No decision document found for given application",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping(value = "/{id}/decision", produces = {"application/pdf", "application/json"})
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<byte[]> getDecision(@PathVariable Integer id) throws IOException {
        validateHasDecision(id);
        byte[] decision = decisionService.getDecision(id);
        List<byte[]> attachments = attachmentService.findDecisionAttachmentsForApplication(id)
                .stream()
                .map(a -> attachmentService.getAttachmentData(a.getId()))
                .collect(Collectors.toList());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_PDF);
        return new ResponseEntity<>(PdfMerger.appendDocuments(decision, attachments), httpHeaders, HttpStatus.OK);
    }

    @Operation(
            summary = "Sends the decision document for given application as email to an specified distribution list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Decision document sent successfully"),
            @ApiResponse(responseCode = "404", description = "No decision document found for given application",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PostMapping(value = "/{id}/decision/send")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<Void> sendDecision(@PathVariable Integer id,
                                             @RequestBody List<DistributionEntryJson> distribution) {
        validateHasDecision(id);
        applicationServiceComposer.sendDecision(id, new DecisionDetailsJson(distribution),
                                                DecisionDocumentType.DECISION);
        return ResponseEntity.ok().build();
    }

    private void validateHasDecision(Integer id) {
        if (applicationServiceComposer.getApplicationType(id) == ApplicationType.NOTE) {
            throw new NoSuchEntityException("note.decision");
        }
    }

}
