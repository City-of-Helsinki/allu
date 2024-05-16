package fi.hel.allu.ui.controller;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.DateReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("/applications")
public class DateReportingController {

  private final DateReportingService dateReportingService;

  @Autowired
  public DateReportingController(DateReportingService dateReportingService) {
    this.dateReportingService = dateReportingService;
  }

  @PutMapping(value = "/{id}/customeroperationalcondition")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_CREATE_APPLICATION')")
  public ResponseEntity<ApplicationJson> reportCustomerOperationalCondition(@PathVariable Integer id,
      @RequestBody @Valid ApplicationDateReport dateReport) {
    return ResponseEntity.ok(dateReportingService.reportCustomerOperationalCondition(id, dateReport));
  }

  @PutMapping(value = "/{id}/customerworkfinished")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_CREATE_APPLICATION')")
  public ResponseEntity<ApplicationJson> reportCustomerWorkFinished(@PathVariable Integer id,
      @RequestBody @Valid ApplicationDateReport dateReport) {
    return ResponseEntity.ok(dateReportingService.reportCustomerWorkFinished(id, dateReport));
  }

  @PutMapping(value = "/{id}/customervalidity")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_CREATE_APPLICATION')")
  public ResponseEntity<ApplicationJson> reportCustomerValidity(@PathVariable Integer id,
                                                            @RequestBody @Valid ApplicationDateReport dateReport) {
    return ResponseEntity.ok(dateReportingService.reportCustomerValidity(id, dateReport));
  }

  @PutMapping(value = "/{id}/operationalcondition")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> reportOperationalCondition(@PathVariable Integer id,
      @RequestBody @NotNull ZonedDateTime operationalConditionDate) {
    return ResponseEntity.ok(dateReportingService.reportOperationalCondition(id, operationalConditionDate));
  }

  @PutMapping(value = "/{id}/workfinished")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> reportWorkFinished(@PathVariable Integer id,
      @RequestBody @NotNull ZonedDateTime workFinishedDate) {
    return ResponseEntity.ok(dateReportingService.reportWorkFinished(id, workFinishedDate));
  }

  @PutMapping(value = "/{id}/locations/{locationId}/customervalidity")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> reportCustomerLocationValidity(@PathVariable Integer id,
      @PathVariable Integer locationId, @RequestBody @Valid ApplicationDateReport dateReport) {
    return ResponseEntity.ok(dateReportingService.reportCustomerLocationValidity(id, locationId, dateReport));
  }
}