package fi.hel.allu.model.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.AttachmentDao;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.model.domain.DefaultAttachmentInfo;
import fi.hel.allu.model.service.event.ApplicationUpdateEvent;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {

  @Autowired
  private AttachmentDao attachmentDao;
  @Autowired
  private ApplicationEventPublisher eventPublisher;

  public AttachmentController() {
  }

  //***********************
  // Attachment APIs
  //***********************
  /**
   * Create new attachment.
   *
   * @throws IOException
   */
  @RequestMapping(value = "/applications/{applicationId}", method = RequestMethod.POST)
  public ResponseEntity<AttachmentInfo> addAttachment(
      @PathVariable int applicationId,
      @Valid @RequestPart("info") AttachmentInfo attachmentInfo, @RequestPart("data") MultipartFile data)
      throws IOException {

    if (attachmentInfo.getId() != null) {
      // this is probably default attachment
      Optional<DefaultAttachmentInfo> defaultAttachmentInfo = attachmentDao.findDefaultById(attachmentInfo.getId());
      if (defaultAttachmentInfo.isPresent()) {
        attachmentDao.linkApplicationToAttachment(applicationId, attachmentInfo.getId());
        eventPublisher.publishEvent(new ApplicationUpdateEvent(applicationId, attachmentInfo.getUserId()));
        return new ResponseEntity<>(attachmentInfo, HttpStatus.CREATED);
      } else {
        throw new NoSuchEntityException("attachment.attach.failed", attachmentInfo.getId());
      }
    } else if (data.isEmpty()) {
      // Empty attachments don't make sense, let's explicitly forbid them
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } else {
      AttachmentInfo inserted = attachmentDao.insert(applicationId, attachmentInfo, data.getBytes());
      eventPublisher.publishEvent(new ApplicationUpdateEvent(applicationId, attachmentInfo.getUserId()));
      return new ResponseEntity<>(inserted, HttpStatus.CREATED);
    }

  }

  @RequestMapping(value = "/applications/{applicationId}/default", method = RequestMethod.PUT)
  public ResponseEntity<Void> addDefaultAttachments(@PathVariable Integer applicationId, @RequestBody List<Integer> defaultAttachmentIds) {
    defaultAttachmentIds.forEach(id -> attachmentDao.linkApplicationToAttachment(applicationId, id));
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/applications/{applicationId}/default", method = RequestMethod.DELETE)
  public ResponseEntity<Void> removeDefaultAttachments(@PathVariable Integer applicationId,  @RequestBody List<Integer> defaultAttachmentIds) {
    defaultAttachmentIds.forEach(id -> attachmentDao.removeLinkApplicationToAttachment(applicationId, id));
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Get the size of attachment data (bytes) by attachment id
   *
   * @param attachmentId
   *          The attachment's ID.
   */
  @RequestMapping(value = "/{attachmentId}/size", method = RequestMethod.GET)
  public ResponseEntity<Long> getAttachmentSize(@PathVariable int attachmentId) {
    Long size = attachmentDao.getSizeByAttachmentId(attachmentId)
        .orElseThrow(() -> new NoSuchEntityException("Attachment data not found for attachment.", Integer.toString(attachmentId)));
    return new ResponseEntity<>(size, HttpStatus.OK);
  }

  /**
   * Get the attachment info by it's id
   *
   * @param attachmentId
   *          The attachment's ID.
   */
  @RequestMapping(value = "/{attachmentId}", method = RequestMethod.GET)
  public ResponseEntity<AttachmentInfo> findAttachmentById(
      @PathVariable int attachmentId) {
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
  public ResponseEntity<AttachmentInfo> updateAttachmentInfo(
      @PathVariable int attachmentId,
      @Valid @RequestBody(required = true) AttachmentInfo attachmentInfo) {
    attachmentDao.findDefaultById(attachmentId)
        .ifPresent(a -> {
          // treating this case as no such entity, because in practice the requested attachment does not exist in "normal" attachment space
          throw new NoSuchEntityException("Attempt to update default attachment as normal attachment", Integer.toString(attachmentId));
        });
    return new ResponseEntity<>(attachmentDao.update(attachmentId, attachmentInfo), HttpStatus.OK);
  }

  /**
   * Delete attachment.
   *
   * @param attachmentId
   *          The attachment's ID.
   */
  @RequestMapping(value = "/applications/{applicationId}/{attachmentId}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteAttachmentInfo(
      @PathVariable int applicationId,
      @PathVariable int attachmentId) {
    Optional<DefaultAttachmentInfo> dai = attachmentDao.findDefaultById(attachmentId, false);
    if (dai.isPresent()) {
      AttachmentInfo ai = attachmentDao.findById(attachmentId).get();
      attachmentDao.removeLinkApplicationToAttachment(applicationId, ai.getId());
    } else {
      attachmentDao.delete(applicationId, attachmentId);
    }
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

  //***********************
  // Default attachment API
  //***********************

  /**
   * Add new default attachment.
   *
   * @param   attachmentInfo    Attachment metadadata
   * @param   data              Attachment binary.
   * @return  Added default attachment.
   * @throws IOException
   */
  @RequestMapping(value = "/default", method = RequestMethod.POST)
  public ResponseEntity<DefaultAttachmentInfo> addDefaultAttachment(
      @Valid @RequestPart("info") DefaultAttachmentInfo attachmentInfo, @RequestPart("data") MultipartFile data)
      throws IOException {

    if (data.isEmpty()) {
      // Empty default attachments don't make sense, let's explicitly forbid them
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    DefaultAttachmentInfo inserted = attachmentDao.insertDefault(attachmentInfo, data.getBytes());
    return new ResponseEntity<>(inserted, HttpStatus.CREATED);
  }

  /**
   * Update default attachment info.
   *
   * @param attachmentId  The attachment's ID.
   * @param attachmentInfo
   *          The new attachment info.
   */
  @RequestMapping(value = "/default/{attachmentId}", method = RequestMethod.PUT)
  public ResponseEntity<AttachmentInfo> updateDefaultAttachmentInfo(
      @PathVariable int attachmentId,
      @Valid @RequestBody(required = true) DefaultAttachmentInfo attachmentInfo) {
    return new ResponseEntity<>(attachmentDao.updateDefault(attachmentId, attachmentInfo), HttpStatus.OK);
  }

  /**
   * Delete default attachment.
   *
   * @param attachmentId
   *          The attachment's ID.
   */
  @RequestMapping(value = "/default/{attachmentId}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteDefaultAttachmentInfo(@PathVariable int attachmentId) {
    attachmentDao.deleteDefault(attachmentId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Find given default attachment.
   *
   * @return  List of default attachments.
   */
  @RequestMapping(value = "/default/{attachmentId}", method = RequestMethod.GET)
  public ResponseEntity<DefaultAttachmentInfo> findAllDefaultAttachmentInfo(
      @PathVariable int attachmentId) {
    DefaultAttachmentInfo attachmentInfo = attachmentDao.findDefaultById(attachmentId)
        .orElseThrow(() -> new NoSuchEntityException("Default attachment not found", Integer.toString(attachmentId)));
    return new ResponseEntity<>(attachmentInfo, HttpStatus.OK);
  }

  /**
   * Find all default attachments.
   *
   * @return  List of default attachments.
   */
  @RequestMapping(value = "/default", method = RequestMethod.GET)
  public ResponseEntity<List<DefaultAttachmentInfo>> findAllDefaultAttachmentInfo() {
    return new ResponseEntity<>(attachmentDao.findDefault(), HttpStatus.OK);
  }

  /**
   * Search default attachments for the given application type.
   *
   * @param   applicationType   Application type whose default attachments should be fetched.
   * @return  List of default attachments for the given application type.
   */
  @RequestMapping(value = "/default/applicationType/{applicationType}", method = RequestMethod.GET)
  public ResponseEntity<List<DefaultAttachmentInfo>> searchDefaultAttachmentInfo(
      @PathVariable String applicationType) {
    return new ResponseEntity<>(attachmentDao.searchDefault(ApplicationType.valueOf(applicationType)), HttpStatus.OK);
  }
}
