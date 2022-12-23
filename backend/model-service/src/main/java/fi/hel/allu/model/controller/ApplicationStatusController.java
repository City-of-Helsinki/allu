package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.ApplicationStatusInfo;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.service.ApplicationStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing application status changes.
 */
@RestController
@RequestMapping("/applications")
public class ApplicationStatusController {

  @Autowired
  ApplicationStatusService applicationStatusService;

  @PutMapping(value = "/{id}/status/pre_reserved")
  public ResponseEntity<Application> changeStatusToPreReserved(@PathVariable int id) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.PRE_RESERVED, null), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/status/pending")
  public ResponseEntity<Application> changeStatusToPending(@PathVariable int id) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.PENDING, null), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/status/pending_client")
  public ResponseEntity<Application> changeStatusToPendingClient(@PathVariable int id) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.PENDING_CLIENT, null), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/status/waiting_information")
  public ResponseEntity<Application> changeStatusToWaitingInformation(@PathVariable int id) {
    return ResponseEntity.ok(applicationStatusService.changeApplicationStatus(id, StatusType.WAITING_INFORMATION, null));
  }

  @PutMapping(value = "/{id}/status/information_received")
  public ResponseEntity<Application> changeStatusToInformationReceived(@PathVariable int id) {
    return ResponseEntity.ok(applicationStatusService.changeApplicationStatus(id, StatusType.INFORMATION_RECEIVED, null));
  }


  @PutMapping(value = "/{id}/status/waiting_contract_approval")
  public ResponseEntity<Application> changeStatusToWaitingContract(@PathVariable int id) {
    return ResponseEntity.ok(applicationStatusService.changeApplicationStatus(id, StatusType.WAITING_CONTRACT_APPROVAL, null));
  }

  @PutMapping(value = "/{id}/status/handling")
  public ResponseEntity<Application> changeStatusToHandling(@PathVariable int id) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.HANDLING, null), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/status/returned_to_preparation")
  public ResponseEntity<Application> changeStatusToReturnedToPreparation(@PathVariable int id) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.RETURNED_TO_PREPARATION, null), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/status/decisionmaking")
  public ResponseEntity<Application> changeStatusToDecisionMaking(@PathVariable int id, @RequestBody Integer userId) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.DECISIONMAKING, userId), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/status/decision")
  public ResponseEntity<Application> changeStatusToDecision(@PathVariable int id, @RequestBody Integer userId) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.DECISION, userId), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/status/rejected")
  public ResponseEntity<Application> changeStatusToRejected(@PathVariable int id, @RequestBody Integer userId) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.REJECTED, userId), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/status/operational_condition")
  public ResponseEntity<Application> changeStatusToOperationalCondition(@PathVariable int id) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.OPERATIONAL_CONDITION, null), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/status/finished")
  public ResponseEntity<Application> changeStatusToFinished(@PathVariable int id) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.FINISHED, null), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/status/cancelled")
  public ResponseEntity<Application> changeStatusToCancelled(@PathVariable int id) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.CANCELLED, null), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/status/archived")
  public ResponseEntity<Application> changeStatusToArchived(@PathVariable int id) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.ARCHIVED, null), HttpStatus.OK);
  }

  @GetMapping(value = "/{id}/status")
  public ResponseEntity<ApplicationStatusInfo> getApplicationStatus(@PathVariable int id) {
    return new ResponseEntity<>(applicationStatusService.getApplicationStatus(id), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/status/return")
  public ResponseEntity<Application> returnToStatus(@PathVariable int id, @RequestBody StatusType status) {
    return new ResponseEntity<>(applicationStatusService.returnToStatus(id, status), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/status/terminated")
  public ResponseEntity<Application> changeStatusToTerminated(@PathVariable int id, @RequestBody Integer userId) {
    return new ResponseEntity<>(applicationStatusService.changeApplicationStatus(id, StatusType.TERMINATED, userId), HttpStatus.OK);
  }
}