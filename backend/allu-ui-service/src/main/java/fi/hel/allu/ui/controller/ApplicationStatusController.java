package fi.hel.allu.ui.controller;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.StatusChangeInfoJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.CommentService;
import fi.hel.allu.servicecore.service.DecisionService;
import fi.hel.allu.ui.security.DecisionSecurityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/applications")
public class ApplicationStatusController {
  private final ApplicationServiceComposer applicationServiceComposer;
  private final CommentService commentService;
  private final DecisionSecurityService decisionSecurityService;
  private final DecisionService decisionService;

  @Autowired
  public ApplicationStatusController(
      ApplicationServiceComposer applicationServiceComposer,
      CommentService commentService,
      DecisionSecurityService decisionSecurityService,
      DecisionService decisionService) {
    this.applicationServiceComposer = applicationServiceComposer;
    this.commentService = commentService;
    this.decisionSecurityService = decisionSecurityService;
    this.decisionService = decisionService;
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
    return ResponseEntity.ok(applicationJson);
  }

  @RequestMapping(value = "/{id}/status/rejected", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_DECISION')")
  public ResponseEntity<ApplicationJson> changeStatusToRejected(
      @PathVariable int id, @RequestBody StatusChangeInfoJson info) {
    commentService.addApplicationRejectComment(id, info.getComment());
    return ResponseEntity.ok(applicationServiceComposer.changeStatus(id, StatusType.REJECTED, info));
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
    return ResponseEntity.ok(applicationServiceComposer.changeStatus(id, StatusType.FINISHED));
  }
}
