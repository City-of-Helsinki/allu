package fi.hel.allu.ui.controller;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.DateReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/applications")
public class DateReportingController {

  private final DateReportingService dateReportingService;

  @Autowired
  public DateReportingController(DateReportingService dateReportingService) {
    this.dateReportingService = dateReportingService;
  }

  @RequestMapping(value = "/{id}/customeroperationalcondition", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_CREATE_APPLICATION')")
  public ResponseEntity<ApplicationJson> reportCustomerOperationalCondition(@PathVariable Integer id,
      @RequestBody @Valid ApplicationDateReport dateReport) {
    return ResponseEntity.ok(dateReportingService.reportCustomerOperationalCondition(id, dateReport));
  }

  @RequestMapping(value = "/{id}/customerworkfinished", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_CREATE_APPLICATION')")
  public ResponseEntity<ApplicationJson> reportCustomerWorkFinished(@PathVariable Integer id,
      @RequestBody @Valid ApplicationDateReport dateReport) {
    return ResponseEntity.ok(dateReportingService.reportCustomerWorkFinished(id, dateReport));
  }

  @RequestMapping(value = "/{id}/customervalidity", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> reportCustomerValidity(@PathVariable Integer id,
                                                            @RequestBody @Valid ApplicationDateReport dateReport) {
    return ResponseEntity.ok(dateReportingService.reportCustomerValidity(id, dateReport));
  }

  @RequestMapping(value = "/{id}/operationalcondition", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> reportOperationalCondition(@PathVariable Integer id,
      @RequestBody @NotNull ZonedDateTime operationalConditionDate) {
    return ResponseEntity.ok(dateReportingService.reportOperationalCondition(id, operationalConditionDate));
  }

  @RequestMapping(value = "/{id}/workfinished", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> reportWorkFinished(@PathVariable Integer id,
      @RequestBody @NotNull ZonedDateTime workFinishedDate) {
    return ResponseEntity.ok(dateReportingService.reportWorkFinished(id, workFinishedDate));
  }

  @RequestMapping(value = "/{id}/locations/{locationId}/customervalidity", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> reportCustomerLocationValidity(@PathVariable Integer id,
      @PathVariable Integer locationId, @RequestBody @Valid ApplicationDateReport dateReport) {
    return ResponseEntity.ok(dateReportingService.reportCustomerLocationValidity(id, locationId, dateReport));
  }
}
