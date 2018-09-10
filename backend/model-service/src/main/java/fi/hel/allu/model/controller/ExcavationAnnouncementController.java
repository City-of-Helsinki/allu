package fi.hel.allu.model.controller;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.ApplicationDateReport;
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

  @RequestMapping(value = "/{id}/operationalcondition", method = RequestMethod.PUT)
  public ResponseEntity<Application> reportOperationalCondition(@PathVariable Integer id, @RequestBody ZonedDateTime operationalConditionDate) {
   return ResponseEntity.ok(applicationService.setOperationalConditionDate(id, operationalConditionDate));
  }

  @RequestMapping(value = "/{id}/workfinished", method = RequestMethod.PUT)
  public ResponseEntity<Application> reportWorkFinished(@PathVariable Integer id, @RequestBody ZonedDateTime workFinishedDate) {
   return ResponseEntity.ok(applicationService.setWorkFinishedDate(id, workFinishedDate));
  }


}
