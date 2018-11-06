package fi.hel.allu.model.controller;

import java.time.ZonedDateTime;

import fi.hel.allu.common.domain.types.ApplicationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.RequiredTasks;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.service.ApplicationService;

@RestController
@RequestMapping("/excavationannouncements")
public class ExcavationAnnouncementController {

  private final ApplicationService applicationService;

  @Autowired
  public ExcavationAnnouncementController(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @RequestMapping(value = "/{id}/customeroperationalcondition", method = RequestMethod.PUT)
  public ResponseEntity<Application> reportCustomerOperationalCondition(@PathVariable Integer id, @RequestBody ApplicationDateReport dateReport) {
   return ResponseEntity.ok(applicationService.setCustomerOperationalConditionDates(id, dateReport));
  }

  @RequestMapping(value = "/{id}/customerworkfinished", method = RequestMethod.PUT)
  public ResponseEntity<Application> reportCustomerWorkFinished(@PathVariable Integer id, @RequestBody ApplicationDateReport dateReport) {
   return ResponseEntity.ok(applicationService.setCustomerWorkFinishedDates(id, dateReport));
  }

  @RequestMapping(value = "/{id}/customervalidity", method = RequestMethod.PUT)
  public ResponseEntity<Application> reportCustomerValidity(@PathVariable Integer id, @RequestBody ApplicationDateReport dateReport) {
    return ResponseEntity.ok(applicationService.setCustomerValidityDates(id, dateReport));
  }

  @RequestMapping(value = "/{id}/operationalcondition", method = RequestMethod.PUT)
  public ResponseEntity<Void> reportOperationalCondition(@PathVariable Integer id, @RequestBody ZonedDateTime operationalConditionDate) {
    applicationService.setOperationalConditionDate(id, operationalConditionDate);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/workfinished", method = RequestMethod.PUT)
  public ResponseEntity<Void> reportWorkFinished(@PathVariable Integer id, @RequestBody ZonedDateTime workFinishedDate) {
   applicationService.setWorkFinishedDate(id, ApplicationType.EXCAVATION_ANNOUNCEMENT, workFinishedDate);
   return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/requiredtasks", method = RequestMethod.PUT)
  public ResponseEntity<Void> setRequiredTasks(@PathVariable Integer id, @RequestBody RequiredTasks tasks) {
    applicationService.setRequiredTasks(id, tasks);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
