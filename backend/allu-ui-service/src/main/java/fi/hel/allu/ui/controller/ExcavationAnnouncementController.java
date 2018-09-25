package fi.hel.allu.ui.controller;

import java.time.ZonedDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.RequiredTasks;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.ui.service.ExcavationAnnouncementService;

@RestController
@RequestMapping("/excavationannouncements")
public class ExcavationAnnouncementController {

  @Autowired
  private ExcavationAnnouncementService excavationAnnouncementService;

  @RequestMapping(value = "/{id}/customeroperationalcondition", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_CREATE_APPLICATION')")
  public ResponseEntity<ApplicationJson> reportCustomerOperationalCondition(@PathVariable Integer id,
      @RequestBody @Valid ApplicationDateReport dateReport) {
    return ResponseEntity.ok(excavationAnnouncementService.reportCustomerOperationalCondition(id, dateReport));
  }

  @RequestMapping(value = "/{id}/customerworkfinished", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_CREATE_APPLICATION')")
  public ResponseEntity<ApplicationJson> reportCustomerWorkFinished(@PathVariable Integer id,
      @RequestBody @Valid ApplicationDateReport dateReport) {
    return ResponseEntity.ok(excavationAnnouncementService.reportCustomerWorkFinished(id, dateReport));
  }

  @RequestMapping(value = "/{id}/operationalcondition", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> reportOperationalCondition(@PathVariable Integer id,
      @RequestBody @NotNull ZonedDateTime operationalConditionDate) {
    return ResponseEntity.ok(excavationAnnouncementService.reportOperationalCondition(id, operationalConditionDate));
  }

  @RequestMapping(value = "/{id}/workfinished", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> reportWorkFinished(@PathVariable Integer id,
      @RequestBody @NotNull ZonedDateTime workFinishedDate) {
    return ResponseEntity.ok(excavationAnnouncementService.reportWorkFinished(id, workFinishedDate));
  }

  @RequestMapping(value = "/{id}/requiredtasks", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION', 'ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> setRequiredTasks(@PathVariable Integer id, @RequestBody RequiredTasks tasks) {
    return ResponseEntity.ok(excavationAnnouncementService.setRequiredTasks(id, tasks));
  }
}
