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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.common.util.PdfMerger;
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

  @ApiOperation(value = "Gets decision document for application with given ID. Returns draft if decision is not yet made.",
      authorizations = @Authorization(value ="api_key"),
      response = byte.class,
      responseContainer = "Array"
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Decision document retrieved successfully", response = byte.class, responseContainer = "Array"),
      @ApiResponse(code = 404, message = "No decision document found for given application", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}/decision", method = RequestMethod.GET, produces = {"application/pdf", "application/json"})
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<byte[]> getDecision(@PathVariable Integer id) throws IOException {
    byte[] decision = decisionService.getDecision(id);
    List<byte[]> attachments = attachmentService.findDecisionAttachmentsForApplication(id)
        .stream()
        .map(a -> attachmentService.getAttachmentData(a.getId()))
        .collect(Collectors.toList());
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_PDF);
    return new ResponseEntity<>(PdfMerger.appendDocuments(decision, attachments), httpHeaders, HttpStatus.OK);
  }

}
