package fi.hel.allu.external.api.controller;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

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

@RestController
@RequestMapping({"/v1", "/v2"})
@Tag(name = "Application attachments")
public class ApplicationAttachmentController {

  @Autowired
  private ApplicationServiceExt applicationService;

  @Operation(summary = "Add new attachment for an application with given ID.",
      description = "Note: This API does not work through swagger. Please use postman to test this API.\n" +
        "In postman, the JSON formatted metadata should be appended as a .json file.",
      security=@SecurityRequirement(name = "api_key"))
  @ApiResponses(value =  {
      @ApiResponse(responseCode = "200", description = "Attachment added successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
  })
  @RequestMapping(value = "/applications/{id}/attachments", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> create(@Parameter(name = "Application ID to add attachment for") @PathVariable Integer id,
                                     @Parameter(name = "Attachment info in JSON", required = true) @Valid @RequestPart(value="metadata",required=true) AttachmentInfoExt metadata,
                                     @Parameter(name = "Attachment data", required = true) @RequestPart(value="file", required=true) MultipartFile file ) throws IOException {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    applicationService.addAttachment(applicationId, metadata, file);
    return new ResponseEntity<>(HttpStatus.OK);
  }


  @Operation(summary = "List decision attachments of an application with given ID",
          security = @SecurityRequirement(name = "api_key"))
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Attachments listed successfully"),
  })
  @RequestMapping(value = "/applications/{id}/attachments", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<List<AttachmentInfoExt>> getAttachments(
          @Parameter(name = "Application ID to get attachments for") @PathVariable Integer id) {

    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    return ResponseEntity.ok(applicationService.getDecisionAttachments(applicationId));
  }

  @Operation(summary = "Get attachment data of an attachment with given attachment ID.",
          security = @SecurityRequirement(name = "api_key"))
  @ApiResponses(value =  {
      @ApiResponse(responseCode = "200", description = "Attachment data fetched successfully",  content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
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
