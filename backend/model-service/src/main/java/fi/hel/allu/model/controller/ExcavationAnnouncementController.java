package fi.hel.allu.model.controller;

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

  @RequestMapping(value = "/{id}/operationalcondition", method = RequestMethod.PUT)
  public ResponseEntity<Application> reportOperationalCondition(@PathVariable Integer id, @RequestBody ApplicationDateReport dateReport) {
   return ResponseEntity.ok(applicationService.setCustomerOperationalConditionDates(id, dateReport));
  }

  @RequestMapping(value = "/{id}/workfinished", method = RequestMethod.PUT)
  public ResponseEntity<Application> reportWorkFinished(@PathVariable Integer id, @RequestBody ApplicationDateReport dateReport) {
   return ResponseEntity.ok(applicationService.setCustomerWorkFinishedDates(id, dateReport));
  }

}
