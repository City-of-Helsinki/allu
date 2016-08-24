package fi.hel.allu.model.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    List<Application> applications = applicationDao.findByLocation(lsc);
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

}
