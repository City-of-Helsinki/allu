package fi.hel.allu.model.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.AttachmentDao;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.model.domain.LocationSearchCriteria;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

  @Autowired
  private ApplicationDao applicationDao;

  @Autowired
  private AttachmentDao attachmentDao;

  @Autowired
  private LocationDao locationDao;

  /**
   * Find application by application ID
   *
   * @param id
   * @return the application
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Application> findById(@PathVariable int id) {
    Optional<Application> application = applicationDao.findById(id);
    Application applicationValue = application
        .orElseThrow(() -> new NoSuchEntityException("Application not found", Integer.toString(id)));
    return new ResponseEntity<>(applicationValue, HttpStatus.OK);
  }

  /**
   * Find all applications for a handler
   *
   * @param handler
   * @return list of applications
   */
  @RequestMapping(value = "/byhandler/{handler}", method = RequestMethod.GET)
  public ResponseEntity<List<Application>> findByHandler(@PathVariable String handler) {
    List<Application> applications = applicationDao.findByHandler(handler);
    return new ResponseEntity<>(applications, HttpStatus.OK);
  }

  /**
   * Find all applications for a project
   *
   * @param projectId
   * @return list of applications
   */
  @RequestMapping(value = "/byproject/{projectId}", method = RequestMethod.GET)
  public ResponseEntity<List<Application>> findByProject(@PathVariable int projectId) {
    List<Application> applications = applicationDao.findByProject(projectId);
    return new ResponseEntity<>(applications, HttpStatus.OK);
  }

  /**
   * Find applications within an area
   *
   * @param lsc
   *          the location search criteria
   * @return All intersecting applications
   */
  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<List<Application>> findByLocation(@Valid @RequestBody LocationSearchCriteria lsc) {
    List<Application> applications = applicationDao.findIntersecting(lsc.getIntersects());
    return new ResponseEntity<>(applications, HttpStatus.OK);
  }

  /**
   * Update existing application
   *
   * @param id
   * @param application
   * @return the updated application
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Application> update(@PathVariable int id,
      @Valid @RequestBody(required = true) Application application) {
    return new ResponseEntity<>(applicationDao.update(id, application), HttpStatus.OK);
  }

  /**
   * Create new application
   *
   * @param application
   *          The application data
   * @return The created application
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Application> insert(@Valid @RequestBody(required = true) Application application) {
    if (application.getId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    return new ResponseEntity<>(applicationDao.insert(application), HttpStatus.OK);
  }

  /**
   * Delete a location from application
   *
   * @param id
   *          application's ID
   * @return
   */
  @RequestMapping(value = "/{id}/location", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteLocation(@PathVariable int id) {
    locationDao.deleteByApplication(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  // Attachment APIs
  /**
   * Add attachment into an application.
   */
  @RequestMapping(value = "/{id}/attachments", method = RequestMethod.POST)
  public ResponseEntity<AttachmentInfo> addAttachment(@PathVariable int id,
      @Valid @RequestBody(required = true) AttachmentInfo attachmentInfo) {
    attachmentInfo.setApplicationId(id);
    AttachmentInfo inserted = attachmentDao.insert(attachmentInfo);
    // build the redirection URI for the HTTP 201 response:
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
        .buildAndExpand(inserted.getId()).toUri());
    return new ResponseEntity<>(inserted, httpHeaders, HttpStatus.CREATED);
  }

  /**
   * Find attachments for an application
   *
   * @param id
   *          The application id
   * @return list of attachments
   */
  @RequestMapping(value = "/{id}/attachments", method = RequestMethod.GET)
  public ResponseEntity<List<AttachmentInfo>> findAttachments(@PathVariable int id) {
    return new ResponseEntity<>(attachmentDao.findByApplication(id), HttpStatus.OK);
  }

  /**
   * Get the attachment info by it's id
   *
   * @param attachmentId The attachment's ID.
   */
  @RequestMapping(value = "/*/attachments/{attachmentId}", method = RequestMethod.GET)
  public ResponseEntity<AttachmentInfo> findAttachmentById(@PathVariable int attachmentId) {
    AttachmentInfo attachmentInfo =
        attachmentDao.findById(attachmentId).orElseThrow(
            () -> new NoSuchEntityException("Attachment not found", Integer.toString(attachmentId)));
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
  @RequestMapping(value = "/*/attachments/{attachmentId}", method = RequestMethod.PUT)
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
  @RequestMapping(value = "/*/attachments/{attachmentId}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteAttachmentInfo(@PathVariable int attachmentId) {
    attachmentDao.delete(attachmentId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Upload the attachment's data.
   */
  @RequestMapping(value = "/*/attachments/{attachmentId}/data", method = RequestMethod.POST)
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
  @RequestMapping(value = "/*/attachments/{attachmentId}/data", method = RequestMethod.GET)
  public ResponseEntity<byte[]> getAttachmentData(@PathVariable int attachmentId) {
    byte[] bytes = attachmentDao.getData(attachmentId).orElseThrow(
        () -> new NoSuchEntityException("Attachment not found", Integer.toString(attachmentId)));
    AttachmentInfo info = attachmentDao.findById(attachmentId).orElseThrow(
        () -> new NoSuchEntityException("Attachment not found", Integer.toString(attachmentId)));
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.parseMediaType(info.getType()));
    return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
  }
}
