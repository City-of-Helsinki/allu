package fi.hel.allu.supervision.api.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1/applications")
@Api(tags = "Applications")
public class DecisionController {

  @Autowired
  private DecisionService decisionService;
  @Autowired
  private AttachmentService attachmentService;
  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;

  @ApiOperation(value = "Gets decision document for application with given ID. Returns draft if decision is not yet made. "
      + "Available for all application types except notes.",
      authorizations = @Authorization(value ="api_key"),
      response = byte.class,
      responseContainer = "Array"
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Decision document retrieved successfully", response = byte.class, responseContainer = "Array"),
      @ApiResponse(code = 404, message = "No decision document found for given application", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}/decision", method = RequestMethod.GET, produces = {"application/pdf", "application/json"})
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

  @ApiOperation(value = "Sends the decision document for given application as email to an specified distribution list.",
      authorizations = @Authorization(value ="api_key")
  )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Decision document sent successfully"),
      @ApiResponse(code = 404, message = "No decision document found for given application", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}/decision/send", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> sendDecision(@PathVariable Integer id,
      @RequestBody List<DistributionEntryJson> distribution) {
    validateHasDecision(id);
    applicationServiceComposer.sendDecision(id, new DecisionDetailsJson(distribution), DecisionDocumentType.DECISION);
    return ResponseEntity.ok().build();
  }

  private void validateHasDecision(Integer id) {
    if (applicationServiceComposer.getApplicationType(id) == ApplicationType.NOTE) {
      throw new NoSuchEntityException("note.decision");
    }
  }

}
