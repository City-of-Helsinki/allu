package fi.hel.allu.model.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import fi.hel.allu.model.dao.DecisionDao;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.model.domain.LocationSearchCriteria;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

  private ApplicationDao applicationDao;

  private AttachmentDao attachmentDao;

  private LocationDao locationDao;

  private DecisionDao decisionDao;

  @Autowired
  public ApplicationController(ApplicationDao applicationDao, AttachmentDao attachmentDao, LocationDao locationDao,
      DecisionDao decisionDao) {
    this.applicationDao = applicationDao;
    this.attachmentDao = attachmentDao;
    this.locationDao = locationDao;
    this.decisionDao = decisionDao;
  }

  /**
   * Find application by application ID
   *
   * @param id
   * @return the application
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Application> findById(@PathVariable int id) {
    List<Application> applications = applicationDao.findByIds(Collections.singletonList(id));
    if (applications.size() != 1) {
      throw new NoSuchEntityException("Application not found", Integer.toString(id));
    }
    return new ResponseEntity<>(applications.get(0), HttpStatus.OK);
  }

  /**
   * Find applications by application IDs
   *
   * @param   List of ids to be searched.
   * @return  found applications
   */
  @RequestMapping(value = "/find", method = RequestMethod.POST)
  public ResponseEntity<List<Application>> findByIds(@RequestBody List<Integer> ids) {
    List<Application> applications = applicationDao.findByIds(ids);
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
   * Updates handler of given applications.
   *
   * @param   handlerId     New handler set to the applications.
   * @param   applications  Applications whose handler is updated.
   */
  @RequestMapping(value = "/handler/{handlerId}", method = RequestMethod.PUT)
  public ResponseEntity<Void> updateHandler(@PathVariable int handlerId, @RequestBody List<Integer> applications) {
    applicationDao.updateHandler(handlerId, applications);
    return new ResponseEntity<Void>(HttpStatus.OK);
  }

  /**
   * Removes handler of given applications.
   *
   * @param   applications  Applications whose handler is removed.
   */
  @RequestMapping(value = "/handler/remove", method = RequestMethod.PUT)
  public ResponseEntity<Void> removeHandler(@RequestBody List<Integer> applications) {
    applicationDao.removeHandler(applications);
    return new ResponseEntity<Void>(HttpStatus.OK);
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

  /**
   * Store the decision PDF for application
   *
   * @param id
   *          application ID
   * @param file
   *          decision PDF data
   * @return
   * @throws IOException
   */
  @RequestMapping(value = "/{id}/decision", method = RequestMethod.POST)
  public ResponseEntity<Void> storeDecision(@PathVariable int id, @RequestParam("file") MultipartFile file)
      throws IOException {
    decisionDao.storeDecision(id, file.getBytes());
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri());
    return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
  }

  /**
   * Get the decision PDF for application
   *
   * @param id
   *          application ID
   * @return Decision PDF data
   *
   */
  @RequestMapping(value = "/{id}/decision", method = RequestMethod.GET)
  public ResponseEntity<byte[]> getDecision(@PathVariable int id) {
    byte[] bytes = decisionDao.getDecision(id)
        .orElseThrow(() -> new NoSuchEntityException("Decision not found", Integer.toString(id)));
    return new ResponseEntity<>(bytes, HttpStatus.OK);
  }
}
