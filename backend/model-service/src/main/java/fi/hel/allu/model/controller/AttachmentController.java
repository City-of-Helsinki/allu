package fi.hel.allu.model.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.AttachmentDao;
import fi.hel.allu.model.domain.AttachmentInfo;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {

  @Autowired
  private AttachmentDao attachmentDao;

  public AttachmentController() {
    // TODO Auto-generated constructor stub
  }

  // Attachment APIs
  /**
   * Create new attachment.
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<AttachmentInfo> addAttachment(
      @Valid @RequestBody(required = true) AttachmentInfo attachmentInfo) {
    AttachmentInfo inserted = attachmentDao.insert(attachmentInfo);
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
   * Upload the attachment's data.
   */
  @RequestMapping(value = "/{attachmentId}/data", method = RequestMethod.POST)
  public ResponseEntity<String> addAttachmentData(@PathVariable int attachmentId,
      @RequestParam("data") MultipartFile data) {
    if (data.isEmpty()) {
      // Empty attachments don't make sense, let's explicitly forbid them
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    try {
      attachmentDao.setData(attachmentId, data.getBytes());
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (IOException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
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
    AttachmentInfo info = attachmentDao.findById(attachmentId)
        .orElseThrow(() -> new NoSuchEntityException("Attachment not found", Integer.toString(attachmentId)));
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.parseMediaType(info.getType()));
    return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
  }

}
