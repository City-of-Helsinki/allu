package fi.hel.allu.supervision.api.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.servicecore.domain.AttachmentInfoJson;
import fi.hel.allu.servicecore.service.AttachmentService;

@RestController
@RequestMapping("/v1")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Application attachments")
public class AttachmentController {

  private final AttachmentService attachmentService;

  public AttachmentController(AttachmentService attachmentService) {
    this.attachmentService = attachmentService;
  }

  @Operation(summary = "Add new attachment for an application with given ID.")
  @ApiResponses(value =  {
      @ApiResponse(responseCode = "200", description = "Attachment added successfully",
              content = @Content( schema = @Schema(implementation = AttachmentInfoJson.class))),
      @ApiResponse(responseCode = "400", description = "Invalid request data",
              content = @Content( schema = @Schema(implementation = ErrorInfo.class)))
  })
  @PostMapping(value = "/applications/{id}/attachments", produces = "application/json", consumes = "multipart/form-data")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<AttachmentInfoJson> create(@Parameter(description = "Application ID to add attachment for") @PathVariable Integer id,
                                     @Parameter(description = "Attachment info in JSON", required = true) @Valid @RequestPart(value="metadata",required=true) AttachmentInfoJson metadata,
                                     @Parameter(description = "Attachment data", required = true) @RequestPart(value="file", required=true) MultipartFile file ) throws IOException {
    return ResponseEntity.ok(attachmentService.addAttachment(id, metadata, file));
  }

  @Operation(summary = "Add default attachments for application.")
  @ApiResponses(value =  {
      @ApiResponse(responseCode = "200", description = "Default attachments added successfully"),
  })
  @PostMapping(value = "/applications/{id}/attachments/default", consumes = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> addDefaultAttachments(@Parameter(description = "Application ID to add default attachment for") @PathVariable Integer id,
                                     @Parameter(description = "Default attachment ID", required = true) @RequestBody List<Integer> defaultAttachmentIds) {
    attachmentService.addDefaultAttachments(id, defaultAttachmentIds);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(summary = "Delete attachment from application.")
  @DeleteMapping(value = "/applications/{id}/attachments/{attachmentid}")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> delete(@Parameter(description = "Application ID") @PathVariable Integer id,
                                     @Parameter(description = "Attachment ID to delete", required = true) @PathVariable(value = "attachmentid") Integer attachmentId) {
    attachmentService.deleteAttachment(id, attachmentId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(summary = "List attachments for an application with given ID.")
  @ApiResponses(value =  {
      @ApiResponse(responseCode = "200", description = "Attachments fetched successfully",
              content = @Content(schema = @Schema(implementation = AttachmentInfoJson.class))),
  })
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
  @GetMapping(value = "/applications/{id}/attachments", produces = "application/json")
  public ResponseEntity<List<AttachmentInfoJson>> getAttachments(@Parameter(description = "Application ID to fetch attachments for") @PathVariable Integer id) {
    return ResponseEntity.ok(attachmentService.findAttachmentsForApplication(id));
  }

  @Operation(summary = "Update decision attachment")
  @ApiResponses(value =  {
    @ApiResponse(responseCode = "200", description = "Decision updated succesfully",
            content = @Content(schema = @Schema(implementation = AttachmentInfoJson.class))),
  })
  @PutMapping(value = "/applications/{id}/decisionAttachment", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<AttachmentInfoJson> updateDecisionAttachment(@Parameter(description = "ID of updated attachment") @PathVariable Integer id,
                                                                     @Parameter(description = "New value for decision attachment") @RequestParam Boolean decisionAttachment) {
    AttachmentInfoJson attachment = attachmentService.getAttachment(id);
    attachment.setDecisionAttachment(decisionAttachment);
   return ResponseEntity.ok(attachmentService.updateAttachment(id, attachment));
  }

  @Operation(summary = "Get attachment data for attachment with given ID.")
  @ApiResponses(value =  {
      @ApiResponse(responseCode = "200", description = "Attachment data fetched successfully",
              content = @Content(schema = @Schema(implementation = byte.class))),
  })
  @GetMapping(value = "/attachments/{attachmentId}/data")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
  public ResponseEntity<byte[]> getAttachmentData(@PathVariable int attachmentId) {
    AttachmentInfoJson info = attachmentService.getAttachment(attachmentId);
    byte[] bytes = attachmentService.getAttachmentData(attachmentId);
    HttpHeaders httpHeaders = new HttpHeaders();
    try {
      httpHeaders.setContentType(MediaType.parseMediaType(info.getMimeType()));
    } catch (InvalidMediaTypeException e) {
      httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    }
    return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
  }

  @Operation(summary = "List default attachments for given application type.")
  @ApiResponses(value =  {
      @ApiResponse(responseCode = "200", description = "Attachments fetched successfully",
              content = @Content(schema = @Schema(implementation = AttachmentInfoJson.class))),
  })
  @GetMapping(value = "/attachments/default/{applicationType}", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
  public ResponseEntity<List<AttachmentInfoJson>> readAttachmentInfos(@PathVariable ApplicationType applicationType) {
    List<AttachmentInfoJson> result = attachmentService.getDefaultAttachmentsByApplicationType(applicationType).stream()
        .map(d -> new AttachmentInfoJson(d.getId(), d.getHandlerName(), d.getType(), d.getMimeType(), d.getName(),
            d.getDescription(), d.getSize(), d.getCreationTime(), d.isDecisionAttachment()))
        .collect(Collectors.toList());
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

}
