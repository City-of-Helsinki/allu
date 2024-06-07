package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.AttachmentDao;
import fi.hel.allu.model.dao.ConfigurationDao;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.model.domain.Configuration;
import fi.hel.allu.model.domain.ConfigurationKey;
import fi.hel.allu.model.domain.DefaultAttachmentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static fi.hel.allu.common.util.AttachmentUtil.bytesToMegabytes;
import static fi.hel.allu.common.util.AttachmentUtil.getFileExtension;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {

  @Autowired
  private AttachmentDao attachmentDao;

  @Autowired
  ConfigurationDao configurationDao;


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
  @PostMapping(value = "/applications/{applicationId}")
  public ResponseEntity<AttachmentInfo> addAttachment(
      @PathVariable int applicationId,
      @Valid @RequestPart("info") AttachmentInfo attachmentInfo, @RequestPart("data") MultipartFile data)
      throws IOException {

    if (attachmentInfo.getId() != null) {
      // this is probably default attachment
      Optional<DefaultAttachmentInfo> defaultAttachmentInfo = attachmentDao.findDefaultById(attachmentInfo.getId());
      if (defaultAttachmentInfo.isPresent()) {
        attachmentDao.linkApplicationToAttachment(applicationId, attachmentInfo.getId());
        return new ResponseEntity<>(attachmentInfo, HttpStatus.CREATED);
      } else {
        throw new NoSuchEntityException("attachment.attach.failed", attachmentInfo.getId());
      }
    } else if (data.isEmpty()) {
      // Empty attachments don't make sense, let's explicitly forbid them
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } else {
      double attachmentSizeInMB = bytesToMegabytes(data.getSize());
      String attachmentExtension = getFileExtension(attachmentInfo.getName());
      String allowedFileTypes = getConfigurationValue(ConfigurationKey.ATTACHMENT_ALLOWED_TYPES);
      int maxFileSize = Integer.parseInt(getConfigurationValue(ConfigurationKey.ATTACHMENT_MAX_SIZE_MB));

      if (!allowedFileTypes.contains(attachmentExtension) || attachmentSizeInMB > maxFileSize) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      AttachmentInfo inserted = attachmentDao.insert(applicationId, attachmentInfo, data.getBytes());
      return new ResponseEntity<>(inserted, HttpStatus.CREATED);
    }
  }

  @PutMapping(value = "/applications/{applicationId}/default")
  public ResponseEntity<Void> addDefaultAttachments(@PathVariable Integer applicationId, @RequestBody List<Integer> defaultAttachmentIds) {
    attachmentDao.linkApplicationToAttachment(applicationId, defaultAttachmentIds);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping(value = "/applications/{applicationId}/default")
  public ResponseEntity<Void> removeDefaultAttachments(@PathVariable Integer applicationId,  @RequestBody List<Integer> defaultAttachmentIds) {
     attachmentDao.removeLinkApplicationToAttachment(applicationId, defaultAttachmentIds);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Get the size of attachment data (bytes) by attachment id
   *
   * @param attachmentId
   *          The attachment's ID.
   */
  @GetMapping(value = "/{attachmentId}/size")
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
  @GetMapping(value = "/{attachmentId}")
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
  @PutMapping(value = "/{attachmentId}")
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
  @DeleteMapping(value = "/applications/{applicationId}/{attachmentId}")
  public ResponseEntity<?> deleteAttachmentInfo(
      @PathVariable int applicationId,
      @PathVariable int attachmentId) {
    Optional<DefaultAttachmentInfo> dai = attachmentDao.findDefaultById(attachmentId, false);
    if (dai.isPresent()) {
      AttachmentInfo ai = attachmentDao.findById(attachmentId).get();
      attachmentDao.removeLinkApplicationToAttachment(applicationId, Collections.singletonList(ai.getId()));
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
  @GetMapping(value = "/{attachmentId}/data")
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
  @PostMapping(value = "/default")
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
  @PutMapping(value = "/default/{attachmentId}")
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
  @DeleteMapping(value = "/default/{attachmentId}")
  public ResponseEntity<?> deleteDefaultAttachmentInfo(@PathVariable int attachmentId) {
    attachmentDao.deleteDefault(attachmentId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Find given default attachment.
   *
   * @return  List of default attachments.
   */
  @GetMapping(value = "/default/{attachmentId}")
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
  @GetMapping(value = "/default")
  public ResponseEntity<List<DefaultAttachmentInfo>> findAllDefaultAttachmentInfo() {
    return new ResponseEntity<>(attachmentDao.findDefault(), HttpStatus.OK);
  }

  /**
   * Search default attachments for the given application type.
   *
   * @param   applicationType   Application type whose default attachments should be fetched.
   * @return  List of default attachments for the given application type.
   */
  @GetMapping(value = "/default/applicationType/{applicationType}")
  public ResponseEntity<List<DefaultAttachmentInfo>> searchDefaultAttachmentInfo(
      @PathVariable String applicationType) {
    return new ResponseEntity<>(attachmentDao.searchDefault(ApplicationType.valueOf(applicationType)), HttpStatus.OK);
  }

  private String getConfigurationValue(ConfigurationKey key) {
    return configurationDao.findByKey(key).stream()
      .findFirst()
      .map(Configuration::getValue)
      .orElseThrow(() -> new NoSuchEntityException("Configuration " + key + " not found."));
  }
}
