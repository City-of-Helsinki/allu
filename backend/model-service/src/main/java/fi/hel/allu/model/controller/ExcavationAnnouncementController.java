package fi.hel.allu.model.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.RequiredTasks;
import fi.hel.allu.model.service.ApplicationService;

@RestController
@RequestMapping("/excavationannouncements")
public class ExcavationAnnouncementController {

  private final ApplicationService applicationService;

  @Autowired
  public ExcavationAnnouncementController(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @RequestMapping(value = "/{id}/requiredtasks", method = RequestMethod.PUT)
  public ResponseEntity<Void> setRequiredTasks(@PathVariable Integer id, @RequestBody RequiredTasks tasks) {
    applicationService.setRequiredTasks(id, tasks);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
