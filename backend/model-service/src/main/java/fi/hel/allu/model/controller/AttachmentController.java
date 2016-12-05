package fi.hel.allu.model.controller;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.AttachmentDao;
import fi.hel.allu.model.domain.AttachmentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {

  @Autowired
  private AttachmentDao attachmentDao;

  public AttachmentController() {
  }

  // Attachment APIs
  /**
   * Create new attachment.
   *
   * @throws IOException
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<AttachmentInfo> addAttachment(
      @Valid @RequestPart("info") AttachmentInfo attachmentInfo, @RequestPart("data") MultipartFile data)
      throws IOException {
    if (data.isEmpty()) {
      // Empty attachments don't make sense, let's explicitly forbid them
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    AttachmentInfo inserted = attachmentDao.insert(attachmentInfo, data.getBytes());
    // build the redirection URI for the HTTP 201 response:
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(
        ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(inserted.getId()).toUri());
    return new ResponseEntity<>(inserted, httpHeaders, HttpStatus.CREATED);
  }

  /**
   * Get the attachment info by it's id
   *
   * @param attachmentId
   *          The attachment's ID.
   */
  @RequestMapping(value = "/{attachmentId}", method = RequestMethod.GET)
  public ResponseEntity<AttachmentInfo> findAttachmentById(@PathVariable int attachmentId) {
    AttachmentInfo attachmentInfo = attachmentDao.findById(attachmentId)
        .orElseThrow(() -> new NoSuchEntityException("Attachment not found", Integer.toString(attachmentId)));
    return new ResponseEntity<>(attachmentInfo, HttpStatus.OK);
  }

  /**
   * Update attachment info.
   *
   * @param attachmentId
   *          The attachment's ID.
   * @param attachmentInfo
   *          The new attachment info.
   */
  @RequestMapping(value = "/{attachmentId}", method = RequestMethod.PUT)
  public ResponseEntity<AttachmentInfo> updateAttachmentInfo(@PathVariable int attachmentId,
      @Valid @RequestBody(required = true) AttachmentInfo attachmentInfo) {
    return new ResponseEntity<>(attachmentDao.update(attachmentId, attachmentInfo), HttpStatus.OK);
  }

  /**
   * Delete attachment.
   *
   * @param attachmentId
   *          The attachment's ID.
   */
  @RequestMapping(value = "/{attachmentId}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteAttachmentInfo(@PathVariable int attachmentId) {
    attachmentDao.delete(attachmentId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Get the attachment's data
   *
   * @param attachmentId
   *          attachment's ID
   * @return The attachment's data
   */
  @RequestMapping(value = "/{attachmentId}/data", method = RequestMethod.GET)
  public ResponseEntity<byte[]> getAttachmentData(@PathVariable int attachmentId) {
    byte[] bytes = attachmentDao.getData(attachmentId)
        .orElseThrow(() -> new NoSuchEntityException("Attachment not found", Integer.toString(attachmentId)));
    return new ResponseEntity<>(bytes, HttpStatus.OK);
  }

}
