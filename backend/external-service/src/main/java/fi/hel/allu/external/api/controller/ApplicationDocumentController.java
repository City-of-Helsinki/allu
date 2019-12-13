package fi.hel.allu.external.api.controller;

import java.util.List;

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
import io.swagger.annotations.*;

@RestController
@RequestMapping({"/v1/documents/applications", "/v2/documents/applications"})
@Api(tags = "Application documents")
public class ApplicationDocumentController {

  @Autowired
  private DecisionService decisionService;

  @Autowired
  private ApprovalDocumentService approvalDocumentService;

  @Autowired
  private DocumentSearchResultMapper resultMapper;

  @ApiOperation(value = "Search decisions with given search criteria.",
      produces = "application/json",
      consumes = "application/json",
      authorizations = @Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Search executed successfully"),
  })
  @RequestMapping(value = "/decisions/search", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL')")
  public ResponseEntity<List<DecisionSearchResult>> searchDecisions(@ApiParam(value = "Decision document search criteria.") @RequestBody DocumentSearchCriteria searchCriteria) {
    List<DecisionSearchResult> result = resultMapper.mapToDecisionSearchResults(decisionService.searchDecisions(searchCriteria));
    return ResponseEntity.ok(result);
  }

  @ApiOperation(value = "Search approval documents (operational condition or work finised approval) with given search criteria.",
      produces = "application/json",
      consumes = "application/json",
      authorizations = @Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Search executed successfully"),
  })
  @RequestMapping(value = "/approval/{type}/search", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL')")
  public ResponseEntity<List<ApprovalDocumentSearchResult>> searchApprovalDocuments(
      @ApiParam(value = "Document type") @PathVariable ApprovalDocumentType type,
      @ApiParam(value = "Document search criteria.") @RequestBody DocumentSearchCriteria searchCriteria) {
    List<ApprovalDocumentSearchResult> result = resultMapper.mapApprovalDocumentSearchResults(approvalDocumentService.searchApprovalDocuments(searchCriteria, type), type);
    return ResponseEntity.ok(result);
  }

  @ApiOperation(value = "Gets decision document for application with given ID. Private person data is anonymized in document.",
      authorizations = @Authorization(value ="api_key"),
      responseContainer = "Array")
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Anonymized decision document retrieved successfully", response = byte.class, responseContainer = "Array"),
      @ApiResponse(code = 404, message = "No decision document found for given application", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}/decision", method = RequestMethod.GET, produces = "application/pdf")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL')")
  public ResponseEntity<byte[]> getDecision(@PathVariable Integer id) {
    return PdfResponseBuilder.createResponseEntity(decisionService.getAnonymizedDecision(id));
  }

  @ApiOperation(value = "Gets approval document of given type for application with given ID. Private person data is anonymized in document.",
      authorizations = @Authorization(value ="api_key"),
      response = byte.class,
      responseContainer = "Array")
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Anonymized approval document retrieved successfully", response = byte.class, responseContainer = "Array"),
      @ApiResponse(code = 404, message = "No approval document found for given application", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}/approval/{type}", method = RequestMethod.GET, produces = "application/pdf")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<byte[]> getApprovalDocument(@PathVariable Integer id, @PathVariable ApprovalDocumentType type) {
    return PdfResponseBuilder.createResponseEntity(approvalDocumentService.getAnonymizedDocument(id, type));
  }
}
