package fi.hel.allu.ui.controller;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.StatusChangeInfoJson;
import fi.hel.allu.servicecore.event.ApplicationArchiveEvent;
import fi.hel.allu.servicecore.service.*;
import fi.hel.allu.ui.security.DecisionSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/applications")
public class ApplicationStatusController {
  private final ApplicationServiceComposer applicationServiceComposer;
  private final CommentService commentService;
  private final DecisionSecurityService decisionSecurityService;
  private final DecisionService decisionService;
  private final ContractService contractService;
  private final ApprovalDocumentService approvalDocumentService;
  private final ChargeBasisService chargeBasisService;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final TerminationService terminationService;

  @Autowired
  public ApplicationStatusController(
      ApplicationServiceComposer applicationServiceComposer,
      CommentService commentService,
      DecisionSecurityService decisionSecurityService,
      DecisionService decisionService,
      ContractService contractService,
      ApprovalDocumentService approvalDocumentService,
      ChargeBasisService chargeBasisService,
      ApplicationEventPublisher applicationEventPublisher,
      TerminationService terminationService) {
    this.applicationServiceComposer = applicationServiceComposer;
    this.commentService = commentService;
    this.decisionSecurityService = decisionSecurityService;
    this.decisionService = decisionService;
    this.contractService = contractService;
    this.approvalDocumentService = approvalDocumentService;
    this.chargeBasisService = chargeBasisService;
    this.applicationEventPublisher = applicationEventPublisher;
    this.terminationService = terminationService;
  }

  @PutMapping(value = "/{id}/status/cancelled")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> changeStatusToCancelled(@PathVariable int id) {
    return ResponseEntity.ok(applicationServiceComposer.changeStatus(
      id, StatusType.CANCELLED, new StatusChangeInfoJson()));
  }

  @PutMapping(value = "/{id}/status/pending")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> changeStatusToPending(@PathVariable int id) {
    return ResponseEntity.ok(applicationServiceComposer.changeStatus(id, StatusType.PENDING));
  }

  @PutMapping(value = "/{id}/status/waiting_information")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> changeStatusToWaitingInformation(@PathVariable int id) {
    return ResponseEntity.ok(applicationServiceComposer.changeStatus(id, StatusType.WAITING_INFORMATION));
  }

  @PutMapping(value = "/{id}/status/waiting_contract_approval")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> changeStatusToWaitingContract(@PathVariable int id) {
    return ResponseEntity.ok(applicationServiceComposer.changeStatus(id, StatusType.WAITING_CONTRACT_APPROVAL));
  }

  @PutMapping(value = "/{id}/status/handling")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> changeStatusToHandling(@PathVariable int id) {
    return ResponseEntity.ok(applicationServiceComposer.changeStatus(id, StatusType.HANDLING));
  }

  @PutMapping(value = "/{id}/status/decisionmaking")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> changeStatusToDecisionMaking(
      @PathVariable int id, @RequestBody(required = false) StatusChangeInfoJson info) {
    if (info != null) {
      commentService.addDecisionProposalComment(id, info);
    }
    return ResponseEntity.ok(applicationServiceComposer.changeStatus(id, StatusType.DECISIONMAKING, info));
  }

  @PutMapping(value = "/{id}/status/decision")
  @PreAuthorize("@decisionSecurityService.canMakeDecision(#id)")
  public ResponseEntity<ApplicationJson> changeStatusToDecision(
      @PathVariable int id, @RequestBody StatusChangeInfoJson info) throws IOException {
    ApplicationJson applicationJson = applicationServiceComposer.changeStatus(id, StatusType.DECISION, info);
    decisionService.generateDecision(id, applicationJson);
    contractService.generateFinalContract(id, applicationJson);
    return ResponseEntity.ok(applicationJson);
  }

  @PutMapping(value = "/{id}/status/rejected")
  @PreAuthorize("hasAnyRole('ROLE_DECISION')")
  public ResponseEntity<ApplicationJson> changeStatusToRejected(
      @PathVariable int id, @RequestBody StatusChangeInfoJson info) {
    commentService.addApplicationRejectComment(id, info.getComment());
    contractService.rejectContractIfExists(id, info.getComment());
    return ResponseEntity.ok(applicationServiceComposer.changeStatus(id, StatusType.REJECTED, info));
  }

  @PutMapping(value = "/{id}/status/operational_condition")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION', 'ROLE_DECISION')")
  public ResponseEntity<ApplicationJson> changeStatusToOperationalCondition(@PathVariable int id) {
    ApplicationJson origApplicationJson = applicationServiceComposer.findApplicationById(id);
    List<ChargeBasisEntry> chargeBasisEntries = chargeBasisService.getUnlockedAndInvoicableChargeBasis(id);
    ApplicationJson applicationJson = applicationServiceComposer.changeStatus(id, StatusType.OPERATIONAL_CONDITION);
    approvalDocumentService.createFinalApprovalDocument(origApplicationJson, applicationJson, chargeBasisEntries);
    return ResponseEntity.ok(applicationJson);
  }

  @PutMapping(value = "/{id}/status/toPreparation")
  @PreAuthorize("hasAnyRole('ROLE_DECISION')")
  public ResponseEntity<ApplicationJson> changeStatusToReturnedToPreparation(
      @PathVariable int id, @RequestBody StatusChangeInfoJson info) {
    commentService.addReturnComment(id, info.getComment());
    return ResponseEntity.ok(applicationServiceComposer.changeStatus(id, StatusType.RETURNED_TO_PREPARATION, info));
  }

  @PutMapping(value = "/{id}/status/finished")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION', 'ROLE_DECISION')")
  public ResponseEntity<ApplicationJson> changeStatusToFinished(@PathVariable int id) {
    ApplicationJson origApplicationJson = applicationServiceComposer.findApplicationById(id);
    List<ChargeBasisEntry> chargeBasisEntries = chargeBasisService.getUnlockedAndInvoicableChargeBasis(id);
    ApplicationJson applicationJson = applicationServiceComposer.changeStatus(id, StatusType.FINISHED);
    approvalDocumentService.createFinalApprovalDocument(origApplicationJson, applicationJson, chargeBasisEntries);
    applicationEventPublisher.publishEvent(new ApplicationArchiveEvent(id));
    return ResponseEntity.ok(applicationJson);
  }

  @PutMapping(value = "/{id}/status/returnToEditing")
  @PreAuthorize("hasAnyRole('ROLE_DECISION')")
  public ResponseEntity<ApplicationJson> returnToEditing(@PathVariable int id, @RequestBody StatusChangeInfoJson info) {
    commentService.addReturnComment(id, info.getComment());
    contractService.rejectContractIfExists(id, info.getComment());
    return ResponseEntity.ok(applicationServiceComposer.returnToEditing(id, info));
  }

  @PutMapping(value = "/{id}/status/terminated")
  @PreAuthorize("hasAnyRole('ROLE_DECISION')")
  public ResponseEntity<ApplicationJson> changeStatusToTerminated(@PathVariable int id) {
    ApplicationJson applicationJson = applicationServiceComposer.changeStatus(id, StatusType.TERMINATED);
    terminationService.generateTermination(id, applicationJson);
    return ResponseEntity.ok(applicationJson);
  }
}