package fi.hel.allu.ui.controller;

import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/applications")
public class ApprovalController {

  private final ApprovalDocumentService approvalDocumentService;
  private final ChargeBasisService chargeBasisService;
  private final DecisionService decisionService;
  private final ApplicationServiceComposer applicationServiceComposer;
  private final BulkApprovalService bulkApprovalService;

  @Autowired
  public ApprovalController(ApprovalDocumentService approvalDocumentService, ChargeBasisService chargeBasisService,
      DecisionService decisionService, ApplicationServiceComposer applicationServiceComposer, BulkApprovalService bulkApprovalService) {
    this.approvalDocumentService = approvalDocumentService;
    this.chargeBasisService = chargeBasisService;
    this.decisionService = decisionService;
    this.applicationServiceComposer = applicationServiceComposer;
    this.bulkApprovalService = bulkApprovalService;
  }

  @GetMapping(value = "/{applicationId}/approvalDocument/{type}")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<byte[]> getApprovalDocument(@PathVariable Integer applicationId, @PathVariable ApprovalDocumentType type) {
    return pdfResult(approvalDocumentService.getApprovalDocument(applicationId, type, chargeBasisService.getUnlockedAndInvoicableChargeBasis(applicationId)));
  }

  protected ResponseEntity<byte[]> pdfResult(byte[] data) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_PDF);
    return new ResponseEntity<>(data, httpHeaders, HttpStatus.OK);
  }

  /**
   * Get the decision PDF for application. If it doesn't exist, generate & return a preview.
   *
   * @param applicationId
   *          the application's Id
   * @return The PDF data
   */
  @GetMapping(value = "/{applicationId}/decision")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<byte[]> getDecision(@PathVariable int applicationId) {
    byte[] bytes = decisionService.getDecision(applicationId);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.parseMediaType("application/pdf"));
    return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
  }

  /**
   * Send the decision PDF for application as email to an updated distribution list.
   *
   * @param applicationId       the application's Id.
   * @param decisionDetailsJson Details of the decision.
   */
  @PostMapping(value = "/{applicationId}/decision/send")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<Void> sendDecision(
    @PathVariable int applicationId,
    @RequestBody @Valid DecisionDetailsJson decisionDetailsJson) {
    applicationServiceComposer.sendDecision(applicationId, decisionDetailsJson, DecisionDocumentType.DECISION);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping(value = "/{applicationId}/operational_condition/send")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<Void> sendOperationalCondition(
    @PathVariable int applicationId,
    @RequestBody @Valid DecisionDetailsJson decisionDetailsJson) {
    applicationServiceComposer.sendDecision(applicationId, decisionDetailsJson, DecisionDocumentType.OPERATIONAL_CONDITION);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping(value = "/{applicationId}/work_finished/send")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<Void> sendWorkFinished(
    @PathVariable int applicationId,
    @RequestBody @Valid DecisionDetailsJson decisionDetailsJson) {
    applicationServiceComposer.sendDecision(applicationId, decisionDetailsJson, DecisionDocumentType.WORK_FINISHED);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping(value = "/{applicationId}/termination/send")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION', 'ROLE_DECISION')")
  public ResponseEntity<Void> sendTermination(
    @PathVariable int applicationId,
    @RequestBody @Valid DecisionDetailsJson decisionDetailsJson) {
    applicationServiceComposer.sendDecision(applicationId, decisionDetailsJson, DecisionDocumentType.TERMINATION);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/distribution")
  @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION', 'ROLE_PROCESS_APPLICATION', 'ROLE_DECISION')")
  public ResponseEntity<List<DistributionEntryJson>> updateDistribution(
    @PathVariable int id,
    @Valid @RequestBody List<DistributionEntryJson> distribution) {
    ApplicationJson applicationJson = applicationServiceComposer.replaceDistributionList(id, distribution);
    return ResponseEntity.ok(applicationJson.getDecisionDistributionList());
  }

  @GetMapping(value = "/{id}/distribution")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<DistributionEntryJson>> getDistribution(@PathVariable int id) {
    return ResponseEntity.ok(applicationServiceComposer.getDistributionList(id));
  }

  @PostMapping(value = "/bulkApprovalEntries")
  @PreAuthorize("hasAnyRole('ROLE_DECISION')")
  public ResponseEntity<List<BulkApprovalEntryJson>> getBulkApprovalEntries(@RequestBody List<Integer> ids) {
    return ResponseEntity.ok(bulkApprovalService.getBulkApprovalEntries(ids));
  }
}