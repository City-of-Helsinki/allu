package fi.hel.allu.model.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.domain.Application;

@RestController
@RequestMapping("/applications")
public class ApplicationController {
  @ExceptionHandler
  void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
  }

  @Inject
  private ApplicationDao applicationDao;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Application> findById(@PathVariable int id) {
    Optional<Application> application = applicationDao.findById(id);
    if (application.isPresent()) {
      return new ResponseEntity<>(application.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(value = "/byhandler/{handler}", method = RequestMethod.GET)
  public ResponseEntity<List<Application>> findByHandler(@PathVariable String handler) {
    List<Application> applications = applicationDao.findByHandler(handler);
    if (applications.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<>(applications, HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/byproject/{projectId}", method = RequestMethod.GET)
  public ResponseEntity<List<Application>> findByProject(@PathVariable int projectId) {
    List<Application> applications = applicationDao.findByProject(projectId);
    if (applications.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<>(applications, HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Application> update(@PathVariable int id,
      @RequestBody(required = true) Application application) {
    return new ResponseEntity<Application>(applicationDao.update(id, application), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Application> insert(@RequestBody(required = true) Application application) {
    if (application.getApplicationId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    return new ResponseEntity<Application>(applicationDao.insert(application), HttpStatus.OK);
  }

}
