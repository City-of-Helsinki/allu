package fi.hel.allu.external.api.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.DocumentSearchCriteria;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.external.domain.ApprovalDocumentSearchResult;
import fi.hel.allu.external.domain.DecisionSearchResult;
import fi.hel.allu.external.mapper.DocumentSearchResultMapper;
import fi.hel.allu.servicecore.service.ApprovalDocumentService;
import fi.hel.allu.servicecore.service.DecisionService;

@RestController
@RequestMapping({"/v1/documents/applications", "/v2/documents/applications"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Application documents")
public class ApplicationDocumentController {

  @Autowired
  private DecisionService decisionService;

  @Autowired
  private ApprovalDocumentService approvalDocumentService;

  @Autowired
  private DocumentSearchResultMapper resultMapper;

  @Operation(summary = "Search decisions with given search criteria.")
  @ApiResponses(value =  {
      @ApiResponse(responseCode = "200", description = "Search executed successfully"),
  })
  @RequestMapping(value = "/decisions/search", method = RequestMethod.POST, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL')")
  public ResponseEntity<List<DecisionSearchResult>> searchDecisions(@Parameter(description = "Decision document search criteria.") @RequestBody DocumentSearchCriteria searchCriteria) {
    List<DecisionSearchResult> result = resultMapper.mapToDecisionSearchResults(decisionService.searchDecisions(searchCriteria));
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "Search approval documents (operational condition or work finised approval) with given search criteria.")
  @ApiResponses(value =  {
      @ApiResponse(responseCode = "200", description = "Search executed successfully"),
  })
  @RequestMapping(value = "/approval/{type}/search", method = RequestMethod.POST, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL')")
  public ResponseEntity<List<ApprovalDocumentSearchResult>> searchApprovalDocuments(
      @Parameter(description = "Document type") @PathVariable ApprovalDocumentType type,
      @Parameter(description = "Document search criteria.") @RequestBody DocumentSearchCriteria searchCriteria) {
    List<ApprovalDocumentSearchResult> result = resultMapper.mapApprovalDocumentSearchResults(approvalDocumentService.searchApprovalDocuments(searchCriteria, type), type);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "Gets decision document for application with given ID. Private person data is anonymized in document.")
  @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Anonymized decision document retrieved successfully", content = @Content(schema =
      @Schema(implementation = byte.class))),
      @ApiResponse(responseCode = "404", description = "No decision document found for given application", content = @Content(schema =
      @Schema(implementation = ErrorInfo.class)))
  })
  @RequestMapping(value = "/{id}/decision", method = RequestMethod.GET, produces = "application/pdf")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL')")
  public ResponseEntity<byte[]> getDecision(@PathVariable Integer id) {
    return PdfResponseBuilder.createResponseEntity(decisionService.getAnonymizedDecision(id));
  }

  @Operation(summary = "Gets approval document of given type for application with given ID. Private person data is anonymized in document.")
  @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Anonymized approval document retrieved successfully", content = @Content(schema =
      @Schema(implementation = byte.class))),
      @ApiResponse(responseCode = "404", description = "No approval document found for given application", content = @Content(schema =
      @Schema(implementation = ErrorInfo.class)))
  })
  @RequestMapping(value = "/{id}/approval/{type}", method = RequestMethod.GET, produces = "application/pdf")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<byte[]> getApprovalDocument(@PathVariable Integer id, @PathVariable ApprovalDocumentType type) {
    return PdfResponseBuilder.createResponseEntity(approvalDocumentService.getAnonymizedDocument(id, type));
  }
}
