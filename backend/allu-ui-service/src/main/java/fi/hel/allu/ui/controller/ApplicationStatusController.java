package fi.hel.allu.ui.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.StatusChangeInfoJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.ApprovalDocumentService;
import fi.hel.allu.servicecore.service.ChargeBasisService;
import fi.hel.allu.servicecore.service.CommentService;
import fi.hel.allu.servicecore.service.ContractService;
import fi.hel.allu.servicecore.service.DecisionService;
import fi.hel.allu.ui.security.DecisionSecurityService;

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

  @Autowired
  public ApplicationStatusController(
      ApplicationServiceComposer applicationServiceComposer,
      CommentService commentService,
      DecisionSecurityService decisionSecurityService,
      DecisionService decisionService,
      ContractService contractService,
      ApprovalDocumentService approvalDocumentService,
      ChargeBasisService chargeBasisService) {
    this.applicationServiceComposer = applicationServiceComposer;
    this.commentService = commentService;
    this.decisionSecurityService = decisionSecurityService;
    this.decisionService = decisionService;
    this.contractService = contractService;
    this.approvalDocumentService = approvalDocumentService;
    this.chargeBasisService = chargeBasisService;
  }

  @RequestMapping(value = "/{id}/status/cancelled", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> changeStatusToCancelled(@PathVariable int id) {
    return ResponseEntity.ok(applicationServiceComposer.changeStatus(
      id, StatusType.CANCELLED, new StatusChangeInfoJson()));
  }

  @RequestMapping(value = "/{id}/status/pending", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> changeStatusToPending(@PathVariable int id) {
    return ResponseEntity.ok(applicationServiceComposer.changeStatus(id, StatusType.PENDING));
  }

  @RequestMapping(value = "/{id}/status/waiting_information", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> changeStatusToWaitingInformation(@PathVariable int id) {
    return ResponseEntity.ok(applicationServiceComposer.changeStatus(id, StatusType.WAITING_INFORMATION));
  }

  @RequestMapping(value = "/{id}/status/waiting_contract_approval", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> changeStatusToWaitingContract(@PathVariable int id) {
    return ResponseEntity.ok(applicationServiceComposer.changeStatus(id, StatusType.WAITING_CONTRACT_APPROVAL));
  }

  @RequestMapping(value = "/{id}/status/handling", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> changeStatusToHandling(@PathVariable int id) {
    return ResponseEntity.ok(applicationServiceComposer.changeStatus(id, StatusType.HANDLING));
  }

  @RequestMapping(value = "/{id}/status/decisionmaking", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> changeStatusToDecisionMaking(
      @PathVariable int id, @RequestBody(required = false) StatusChangeInfoJson info) {
    if (info != null) {
      commentService.addDecisionProposalComment(id, info);
    }
    return ResponseEntity.ok(applicationServiceComposer.changeStatus(id, StatusType.DECISIONMAKING, info));
  }

  @RequestMapping(value = "/{id}/status/decision", method = RequestMethod.PUT)
  @PreAuthorize("@decisionSecurityService.canMakeDecision(#id)")
  public ResponseEntity<ApplicationJson> changeStatusToDecision(
      @PathVariable int id, @RequestBody StatusChangeInfoJson info) throws IOException {
    ApplicationJson applicationJson = applicationServiceComposer.changeStatus(id, StatusType.DECISION, info);
    decisionService.generateDecision(id, applicationJson);
    contractService.generateFinalContract(id, applicationJson);
    return ResponseEntity.ok(applicationJson);
  }

  @RequestMapping(value = "/{id}/status/rejected", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_DECISION')")
  public ResponseEntity<ApplicationJson> changeStatusToRejected(
      @PathVariable int id, @RequestBody StatusChangeInfoJson info) {
    commentService.addApplicationRejectComment(id, info.getComment());
    contractService.rejectContractIfExists(id, info.getComment());
    return ResponseEntity.ok(applicationServiceComposer.changeStatus(id, StatusType.REJECTED, info));
  }

  @RequestMapping(value = "/{id}/status/operational_condition", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> changeStatusToOperationalCondition(@PathVariable int id) {
    ApplicationJson origApplicationJson = applicationServiceComposer.findApplicationById(id);
    List<ChargeBasisEntry> chargeBasisEntries = chargeBasisService.getUnlockedChargeBasis(id);
    ApplicationJson applicationJson = applicationServiceComposer.changeStatus(id, StatusType.OPERATIONAL_CONDITION);
    approvalDocumentService.createFinalApprovalDocument(origApplicationJson, applicationJson, chargeBasisEntries);
    return ResponseEntity.ok(applicationJson);
  }

  @RequestMapping(value = "/{id}/status/toPreparation", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_DECISION')")
  public ResponseEntity<ApplicationJson> changeStatusToReturnedToPreparation(
      @PathVariable int id, @RequestBody StatusChangeInfoJson info) {
    commentService.addReturnComment(id, info.getComment());
    return ResponseEntity.ok(applicationServiceComposer.changeStatus(id, StatusType.RETURNED_TO_PREPARATION, info));
  }

  @RequestMapping(value = "/{id}/status/finished", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> changeStatusToFinished(@PathVariable int id) {
    ApplicationJson origApplicationJson = applicationServiceComposer.findApplicationById(id);
    List<ChargeBasisEntry> chargeBasisEntries = chargeBasisService.getUnlockedChargeBasis(id);
    ApplicationJson applicationJson = applicationServiceComposer.changeStatus(id, StatusType.FINISHED);
    approvalDocumentService.createFinalApprovalDocument(origApplicationJson, applicationJson, chargeBasisEntries);
    return ResponseEntity.ok(applicationJson);
  }
}
