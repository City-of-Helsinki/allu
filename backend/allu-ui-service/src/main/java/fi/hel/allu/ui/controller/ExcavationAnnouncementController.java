package fi.hel.allu.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.SupervisionTaskService;

@RestController
@RequestMapping("/excavationannouncements")
public class ExcavationAnnouncementController {

  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;

  @Autowired
  private SupervisionTaskService supervisionTaskService;


  @RequestMapping(value = "/{id}/operationalcondition", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> reportOperationalCondition(@PathVariable Integer id, @RequestBody ApplicationDateReport dateReport) {
    ApplicationJson application = applicationServiceComposer.setCustomerOperationalConditionDates(id, dateReport);
    supervisionTaskService.updateSupervisionTaskDate(id, SupervisionTaskType.OPERATIONAL_CONDITION, dateReport.getReportedDate());
    return ResponseEntity.ok(application);

  }

  @RequestMapping(value = "/{id}/workfinished", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> reportWorkFinished(@PathVariable Integer id, @RequestBody ApplicationDateReport dateReport) {
   ApplicationJson setCustomerWorkFinishedDates = applicationServiceComposer.setCustomerWorkFinishedDates(id, dateReport);
   supervisionTaskService.updateSupervisionTaskDate(id, SupervisionTaskType.FINAL_SUPERVISION, dateReport.getReportedDate());
   return ResponseEntity.ok(setCustomerWorkFinishedDates);
  }


}
