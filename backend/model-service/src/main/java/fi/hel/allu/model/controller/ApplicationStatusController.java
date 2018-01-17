package fi.hel.allu.model.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.service.ApplicationStatusService;

/**
 * Controller for managing application status changes.
 */
@RestController
@RequestMapping("/applications")
public class ApplicationStatusController {

  @Autowired
  ApplicationStatusService applicationStatusService;

  @RequestMapping(value = "/{id}/status/pre_reserved", method = RequestMethod.PUT)
  public ResponseEntity<Application> changeStatusToPreReserved(@PathVariable int id) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.PRE_RESERVED, null), HttpStatus.OK);
  }


  @RequestMapping(value = "/{id}/status/handling", method = RequestMethod.PUT)
  public ResponseEntity<Application> changeStatusToHandling(@PathVariable int id) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.HANDLING, null), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/status/returned_to_preparation", method = RequestMethod.PUT)
  public ResponseEntity<Application> changeStatusToReturnedToPreparation(@PathVariable int id) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.RETURNED_TO_PREPARATION, null), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/status/decisionmaking", method = RequestMethod.PUT)
  public ResponseEntity<Application> changeStatusToDecisionMaking(@PathVariable int id) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.DECISIONMAKING, null), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/status/decision", method = RequestMethod.PUT)
  public ResponseEntity<Application> changeStatusToDecision(@PathVariable int id, @RequestBody Integer userId) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.DECISION, userId), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/status/rejected", method = RequestMethod.PUT)
  public ResponseEntity<Application> changeStatusToRejected(@PathVariable int id, @RequestBody Integer userId) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.REJECTED, userId), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/status/finished", method = RequestMethod.PUT)
  public ResponseEntity<Application> changeStatusToFinished(@PathVariable int id) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.FINISHED, null), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/status/cancelled", method = RequestMethod.PUT)
  public ResponseEntity<Application> changeStatusToCancelled(@PathVariable int id) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.CANCELLED, null), HttpStatus.OK);
  }
}
