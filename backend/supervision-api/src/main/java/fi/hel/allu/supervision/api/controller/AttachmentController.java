package fi.hel.allu.supervision.api.controller;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.servicecore.domain.AttachmentInfoJson;
import fi.hel.allu.servicecore.service.AttachmentService;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1")
@Api
public class AttachmentController {

  @Autowired
  private AttachmentService attachmentService;

  @ApiOperation(value = "Add new attachment for an application with given ID.",
      produces = "application/json",
      consumes = "multipart/form-data",
      response = AttachmentInfoJson.class,
      authorizations = @Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Attachment added successfully", response = AttachmentInfoJson.class),
      @ApiResponse(code = 400, message = "Invalid request data", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/applications/{id}/attachments", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<AttachmentInfoJson> create(@ApiParam(value = "Application ID to add attachment for") @PathVariable Integer id,
                                     @ApiParam(value = "Attachment info in JSON", required = true) @Valid @RequestPart(value="metadata",required=true) AttachmentInfoJson metadata,
                                     @ApiParam(value = "Attachment data", required = true) @RequestPart(value="file", required=true) MultipartFile file ) throws IOException {
    return ResponseEntity.ok(attachmentService.addAttachment(id, metadata, file));
  }

  @ApiOperation(value = "List attachments for an application with given ID.",
      produces = "application/json",
      response = AttachmentInfoJson.class,
      responseContainer = "List",
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Attachments fetched successfully", response = AttachmentInfoJson.class, responseContainer="List"),
  })
  @RequestMapping(value = "/applications/{id}/attachments", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<List<AttachmentInfoJson>> getAttachments(@ApiParam(value = "Application ID to fetch attachments for") @PathVariable Integer id) {
    return ResponseEntity.ok(attachmentService.findAttachmentsForApplication(id));
  }

  @ApiOperation(value = "Get attachment data for attachment with given ID.",
      response = byte.class,
      responseContainer = "Array",
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Attachment data fetched successfully", response = byte.class, responseContainer="Array"),
  })
  @RequestMapping(value = "/attachments/{attachmentId}/data", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<byte[]> getAttachmentData(@PathVariable int attachmentId) {
    AttachmentInfoJson info = attachmentService.getAttachment(attachmentId);
    byte[] bytes = attachmentService.getAttachmentData(attachmentId);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Content-Disposition", "attachment; filename=" + info.getName());
    try {
      httpHeaders.setContentType(MediaType.parseMediaType(info.getMimeType()));
    } catch (InvalidMediaTypeException e) {
      httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    }
    return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
  }


}
