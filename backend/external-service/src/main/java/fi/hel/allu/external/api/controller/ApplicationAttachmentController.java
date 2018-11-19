package fi.hel.allu.external.api.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

}
