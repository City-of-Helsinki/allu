package fi.hel.allu.external.api.controller;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.external.domain.AttachmentInfoExt;
import fi.hel.allu.external.service.ApplicationServiceExt;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1")
@Api(tags = "Application attachments")
public class ApplicationAttachmentController {

  @Autowired
  private ApplicationServiceExt applicationService;

  @ApiOperation(value = "Add new attachment for an application with given ID.",
      produces = "application/json",
      consumes = "multipart/form-data",
      response = Integer.class,
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Attachment added successfully", response = Void.class),
      @ApiResponse(code = 400, message = "Invalid request data", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/applications/{id}/attachments", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> create(@ApiParam(value = "Application ID to add attachment for") @PathVariable Integer id,
                                     @ApiParam(value = "Attachment info in JSON", required = true) @Valid @RequestPart(value="metadata",required=true) AttachmentInfoExt metadata,
                                     @ApiParam(value = "Attachment data", required = true) @RequestPart(value="file", required=true) MultipartFile file ) throws IOException {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    applicationService.addAttachment(applicationId, metadata, file);
    return new ResponseEntity<>(HttpStatus.OK);
  }


  @ApiOperation(value = "List decision attachments of an application with given ID",
      produces = "application/json",
      response = AttachmentInfoExt.class,
      responseContainer = "List",
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Attachments listed successfully", response = Void.class),
  })
  @RequestMapping(value = "/applications/{id}/attachments", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<List<AttachmentInfoExt>> getAttachments(@ApiParam(value = "Application ID to get attachments for") @PathVariable Integer id) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    return ResponseEntity.ok(applicationService.getDecisionAttachments(applicationId));
  }

  @ApiOperation(value = "Get attachment data of an attachment with given attachment ID.",
      response = byte.class,
      responseContainer = "Array",
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Attachment data fetched successfully", response = byte.class, responseContainer="Array"),
  })
  @RequestMapping(value = "/applications/{id}/attachments/{attachmentId}/data", method = RequestMethod.GET, produces = "application/pdf")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<byte[]> getAttachmentData(@PathVariable(value = "id") Integer id, @PathVariable(value = "attachmentId") Integer attachmentId) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    AttachmentInfoExt info = applicationService.getDecisionAttachmentInfo(applicationId, attachmentId);
    byte[] attachmentData = applicationService.getDecisionAttachmentData(attachmentId);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Content-Disposition", "attachment; filename=" + info.getName());
    httpHeaders.setContentType(MediaType.parseMediaType(info.getMimeType()));
    return new ResponseEntity<>(attachmentData, httpHeaders, HttpStatus.OK);
  }

}
