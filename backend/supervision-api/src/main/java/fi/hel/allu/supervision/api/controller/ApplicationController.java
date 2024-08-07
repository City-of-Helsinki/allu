package fi.hel.allu.supervision.api.controller;


import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ApplicationTagJson;
import fi.hel.allu.servicecore.domain.StatusChangeInfoJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.ChargeBasisService;
import fi.hel.allu.servicecore.service.CommentService;
import fi.hel.allu.supervision.api.domain.DecisionInfo;
import fi.hel.allu.supervision.api.validation.DecisionMakerValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Common operations for all application types
 */
@RestController
@RequestMapping("/v1")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Applications")
public class ApplicationController {

  private final ApplicationServiceComposer applicationServiceComposer;
  private final CommentService commentService;
  private final ChargeBasisService chargeBasisService;
  private final DecisionMakerValidator decisionMakerValidator;


  public ApplicationController(ApplicationServiceComposer applicationServiceComposer, CommentService commentService,
                               ChargeBasisService chargeBasisService, DecisionMakerValidator decisionMakerValidator) {
    this.applicationServiceComposer = applicationServiceComposer;
    this.commentService = commentService;
    this.chargeBasisService = chargeBasisService;
    this.decisionMakerValidator = decisionMakerValidator;
  }

  @InitBinder("decisionInfo")
  protected void initUpdateBinder(WebDataBinder binder) {
    binder.addValidators(decisionMakerValidator);
  }

  @Operation(summary = "Update application owner.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Application owner updated successfully"),
  })
  @PutMapping(value = "/applications/{id}/owner",  produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> updateOwner(@PathVariable Integer id,
                                          @Parameter(description = "Id of the new owner") @RequestParam Integer ownerId) {
    applicationServiceComposer.updateApplicationOwner(ownerId, Collections.singletonList(id), true);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Remove application owner.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Application owner removed successfully"),
  })
  @DeleteMapping(value = "/applications/{id}/owner", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> removeOwner(@PathVariable Integer id) {
    applicationServiceComposer.removeApplicationOwner(Collections.singletonList(id), true);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Move application to handling. Allowed if current state is pending.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Application moved to handling successfully"),
  })
  @PutMapping(value = "/applications/{id}/handling", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> moveToHandling(@PathVariable Integer id) {
    validateApplicationStatus(id, Collections.singletonList(StatusType.PENDING));
    applicationServiceComposer.changeStatus(id, StatusType.HANDLING);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Cancel application. Allowed if current state is decision or earlier")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Application cancelled successfully"),
  })
  @PutMapping(value = "/applications/{id}/cancelled", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> cancel(@PathVariable Integer id) {
    List<StatusType> cancellationAllowedStatuses = Stream.of(StatusType.values())
      .filter(s -> s.isBeforeDecision() || s == StatusType.DECISION)
      .collect(Collectors.toList());
    validateApplicationStatus(id, cancellationAllowedStatuses);
    applicationServiceComposer.changeStatus(id, StatusType.CANCELLED);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Move application to decision making. " +
    "Allowed if current state is handling or returned to preparation.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Application moved to decision making successfully"),
  })
  @PutMapping(value = "/applications/{id}/decisionmaking", consumes = "application/json",   produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> moveToDecisionMaking(@PathVariable Integer id, @RequestBody @Valid DecisionInfo decisionInfo) {
    validateApplicationStatus(id,  Stream.of(StatusType.HANDLING, StatusType.RETURNED_TO_PREPARATION).collect(Collectors.toList()));
    validateDecisionMakingAllowed(id);
    StatusChangeInfoJson statusChangeInfo = new StatusChangeInfoJson(
      CommentType.PROPOSE_APPROVAL, decisionInfo.getDecisionNote(), decisionInfo.getDecisionMakerId());
    commentService.addDecisionProposalComment(id, statusChangeInfo);
    applicationServiceComposer.changeStatus(id, StatusType.DECISIONMAKING, statusChangeInfo);
    return ResponseEntity.ok().build();
  }

  private void validateApplicationStatus(Integer id, List<StatusType> allowedStatuses) {
    if (!allowedStatuses.contains(getCurrentStatus(id))) {
      throw new IllegalOperationException("application.statuschange.forbidden");
    }
  }

  private StatusType getCurrentStatus(Integer id) {
    return applicationServiceComposer.getApplicationStatus(id).getStatus();
  }

  private void validateDecisionMakingAllowed(Integer id) {
    if (hasDecisionBlockingTags(id)) {
      throw new IllegalOperationException("application.decision.blockingtags");
    }
    if (!hasValidInvoicing(id)) {
      throw new IllegalOperationException("application.invoicing.invalid");
    }
  }

  private boolean hasValidInvoicing(Integer id) {
    ApplicationJson application = applicationServiceComposer.findApplicationById(id);
    return BooleanUtils.isTrue(application.getNotBillable())
      || (application.getInvoiceRecipientId() != null && hasInvoicing(id));
  }

  private boolean hasInvoicing(Integer applicationId) {
    return !CollectionUtils.isEmpty(chargeBasisService.getChargeBasis(applicationId));
  }

  private boolean hasDecisionBlockingTags(Integer id) {
    return applicationServiceComposer.findTags(id)
      .stream()
      .anyMatch(this::blocksDecisionMaking);
  }

  private boolean blocksDecisionMaking(ApplicationTagJson t) {
    return t.getType() == ApplicationTagType.DATE_CHANGE || t.getType() == ApplicationTagType.OTHER_CHANGES;
  }

}
