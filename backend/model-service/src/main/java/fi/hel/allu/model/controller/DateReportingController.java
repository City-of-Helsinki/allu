package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.service.ApplicationService;
import fi.hel.allu.model.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
   applicationService.setWorkFinishedDate(id, workFinishedDate);
   return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/locations/{locationId}/customervalidity", method = RequestMethod.PUT)
  public ResponseEntity<Void> reportCustomerLocationValidity(@PathVariable Integer id,
      @PathVariable Integer locationId, @RequestBody ApplicationDateReport dateReport) {
    locationService.setCustomerLocationValidity(locationId, dateReport);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
