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
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.LocationSearchCriteria;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

  @Autowired
  private ApplicationDao applicationDao;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Application> findById(@PathVariable int id) {
    Optional<Application> application = applicationDao.findById(id);
    Application applicationValue = application
        .orElseThrow(() -> new NoSuchEntityException("Application not found", Integer.toString(id)));
    return new ResponseEntity<>(applicationValue, HttpStatus.OK);
  }

  @RequestMapping(value = "/byhandler/{handler}", method = RequestMethod.GET)
  public ResponseEntity<List<Application>> findByHandler(@PathVariable String handler) {
    List<Application> applications = applicationDao.findByHandler(handler);
    return new ResponseEntity<>(applications, HttpStatus.OK);
  }

  @RequestMapping(value = "/byproject/{projectId}", method = RequestMethod.GET)
  public ResponseEntity<List<Application>> findByProject(@PathVariable int projectId) {
    List<Application> applications = applicationDao.findByProject(projectId);
    return new ResponseEntity<>(applications, HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.GET)
  public ResponseEntity<List<Application>> findByLocation(@Valid @RequestBody LocationSearchCriteria lsc) {
    List<Application> applications = applicationDao.findIntersecting(lsc.getIntersects());
    return new ResponseEntity<>(applications, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Application> update(@PathVariable int id,
      @Valid @RequestBody(required = true) Application application) {
    return new ResponseEntity<>(applicationDao.update(id, application), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Application> insert(@Valid @RequestBody(required = true) Application application) {
    if (application.getId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    return new ResponseEntity<>(applicationDao.insert(application), HttpStatus.OK);
  }

}
