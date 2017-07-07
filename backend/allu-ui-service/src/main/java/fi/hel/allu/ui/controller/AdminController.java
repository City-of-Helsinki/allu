package fi.hel.allu.ui.controller;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.ui.domain.DefaultAttachmentInfoJson;
import fi.hel.allu.ui.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * Controller for administrative functionality.
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

  @Autowired
  private AttachmentService attachmentService;

  // Default Attachment modification API

  /**
   * Read default attachment info by ID.
   *
   * @param id          attachment ID
   * @return attachment info for the ID.
   */
  @RequestMapping(value = "/attachments/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<DefaultAttachmentInfoJson> readAttachmentInfo(@PathVariable int id) {
    return new ResponseEntity<>(attachmentService.getDefaultAttachment(id), HttpStatus.OK);
  }

  /**
   * Read all default attachments.
   *
   * @return attachment info of all default attachments.
   */
  @RequestMapping(value = "/attachments", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<List<DefaultAttachmentInfoJson>> readAttachmentInfos() {
    return new ResponseEntity<>(attachmentService.getDefaultAttachments(), HttpStatus.OK);
  }

  /**
   * Read default attachments for given application type.
   *
   * @return attachment info of all default attachments.
   */
  @RequestMapping(value = "/attachments/applicationType/{applicationType}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<List<DefaultAttachmentInfoJson>> readAttachmentInfos(@PathVariable ApplicationType applicationType) {
    return new ResponseEntity<>(attachmentService.getDefaultAttachmentsByApplicationType(applicationType), HttpStatus.OK);
  }

  /**
   * Add attachments to application
   *
   * @param infos   Array of attachment's infos
   * @param files   Matching files (files[0] for infos[0] etc.)
   *
   * @return Updated result infos
   * @throws IOException
   * @throws IllegalArgumentException
   */
  @RequestMapping(value = "/attachments", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<List<DefaultAttachmentInfoJson>> addAttachments(
      @RequestPart("meta") @Valid DefaultAttachmentInfoJson[] infos, @RequestPart("file") MultipartFile[] files)
      throws IllegalArgumentException, IOException {
    return new ResponseEntity<>(attachmentService.addDefaultAttachments(infos, files), HttpStatus.CREATED);
  }

  /**
   * Delete attachment
   *
   * @param id  attachment ID
   * @return Nothing
   */
  @RequestMapping(value = "/attachments/{id}", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<Void> deleteAttachment(@PathVariable int id) {
    attachmentService.deleteDefaultAttachment(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Update existing default attachment info.
   *
   * @param id  default attachment ID
   * @param attachmentInfoJson
   * @return  updated default attachment.
   */
  @RequestMapping(value = "/attachments/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<DefaultAttachmentInfoJson> updateAttachmentInfo(
      @PathVariable int id,
      @Valid @RequestBody DefaultAttachmentInfoJson attachmentInfoJson) {
    return new ResponseEntity<>(attachmentService.updateDefaultAttachment(id, attachmentInfoJson), HttpStatus.OK);
  }
}
