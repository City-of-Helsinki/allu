package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.service.ApplicationService;
import fi.hel.allu.model.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/applications")
public class DateReportingController {

  private final ApplicationService applicationService;
  private final LocationService locationService;

  @Autowired
  public DateReportingController(ApplicationService applicationService, LocationService locationService) {
    this.applicationService = applicationService;
    this.locationService = locationService;
  }

  @PutMapping(value = "/{id}/customeroperationalcondition")
  public ResponseEntity<Application> reportCustomerOperationalCondition(@PathVariable Integer id, @RequestBody ApplicationDateReport dateReport) {
   return ResponseEntity.ok(applicationService.setCustomerOperationalConditionDates(id, dateReport));
  }

  @PutMapping(value = "/{id}/customerworkfinished")
  public ResponseEntity<Application> reportCustomerWorkFinished(@PathVariable Integer id, @RequestBody ApplicationDateReport dateReport) {
   return ResponseEntity.ok(applicationService.setCustomerWorkFinishedDates(id, dateReport));
  }

  @PutMapping(value = "/{id}/customervalidity")
  public ResponseEntity<Application> reportCustomerValidity(@PathVariable Integer id, @RequestBody ApplicationDateReport dateReport) {
    return ResponseEntity.ok(applicationService.setCustomerValidityDates(id, dateReport));
  }

  @PutMapping(value = "/{id}/operationalcondition")
  public ResponseEntity<Void> reportOperationalCondition(@PathVariable Integer id, @RequestBody ZonedDateTime operationalConditionDate) {
    applicationService.setOperationalConditionDate(id, operationalConditionDate);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/workfinished")
  public ResponseEntity<Void> reportWorkFinished(@PathVariable Integer id, @RequestBody ZonedDateTime workFinishedDate) {
   applicationService.setWorkFinishedDate(id, workFinishedDate);
   return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/locations/{locationId}/customervalidity")
  public ResponseEntity<Void> reportCustomerLocationValidity(@PathVariable Integer id,
      @PathVariable Integer locationId, @RequestBody ApplicationDateReport dateReport) {
    locationService.setCustomerLocationValidity(locationId, dateReport);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}