package fi.hel.allu.supervision.api.controller;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

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
import io.swagger.annotations.*;

/**
 * Common operations for all application types
 */
@RestController
@RequestMapping("/v1")
@Api(tags = "Applications")
public class ApplicationController {

  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;
  @Autowired
  private CommentService commentService;
  @Autowired
  private ChargeBasisService chargeBasisService;
  @Autowired
  private DecisionMakerValidator decisionMakerValidator;

  @InitBinder("decisionInfo")
  protected void initUpdateBinder(WebDataBinder binder) {
    binder.addValidators(decisionMakerValidator);
  }

  @ApiOperation(value = "Update application owner.",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Application owner updated successfully"),
  })
  @RequestMapping(value = "/applications/{id}/owner", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> updateOwner(@PathVariable Integer id,
      @ApiParam(value = "Id of the new owner") @RequestParam Integer ownerId) {
    applicationServiceComposer.updateApplicationOwner(ownerId, Collections.singletonList(id), true);
    return ResponseEntity.ok().build();
  }

  @ApiOperation(value = "Remove application owner.",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Application owner removed successfully"),
  })
  @RequestMapping(value = "/applications/{id}/owner", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> removeOwner(@PathVariable Integer id) {
    applicationServiceComposer.removeApplicationOwner(Collections.singletonList(id), true);
    return ResponseEntity.ok().build();
  }

  @ApiOperation(value = "Move application to handling. Allowed if current state is pending.",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Application moved to handling successfully"),
  })
  @RequestMapping(value = "/applications/{id}/handling", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> moveToHandling(@PathVariable Integer id) {
    validateApplicationStatus(id, StatusType.PENDING);
    applicationServiceComposer.changeStatus(id, StatusType.HANDLING);
    return ResponseEntity.ok().build();
  }

  @ApiOperation(value = "Cancel application. Allowed if current state is decision or earlier",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Application cancelled successfully"),
  })
  @RequestMapping(value = "/applications/{id}/cancelled", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> cancel(@PathVariable Integer id) {
    List<StatusType> cancellationAllowedStatuses = Stream.of(StatusType.values())
        .filter(s -> s.isBeforeDecision() || s == StatusType.DECISION)
        .collect(Collectors.toList());
    validateApplicationStatus(id, cancellationAllowedStatuses);
    applicationServiceComposer.changeStatus(id, StatusType.CANCELLED);
    return ResponseEntity.ok().build();
  }

  @ApiOperation(value = "Move application to decision making. Allowed if current state is handling.",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Application moved to decision making successfully"),
  })
  @RequestMapping(value = "/applications/{id}/decisionmaking", method = RequestMethod.PUT, consumes = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> moveToDecisionMaking(@PathVariable Integer id, @RequestBody @Valid DecisionInfo decisionInfo) {
    validateApplicationStatus(id, StatusType.HANDLING);
    validateDecisionMakingAllowed(id);
    StatusChangeInfoJson statusChangeInfo = new StatusChangeInfoJson(CommentType.PROPOSE_APPROVAL, decisionInfo.getDecisionNote(), decisionInfo.getDecisionMakerId());
    commentService.addDecisionProposalComment(id, statusChangeInfo);
    applicationServiceComposer.changeStatus(id, StatusType.DECISIONMAKING, statusChangeInfo);
    return ResponseEntity.ok().build();
  }

  private void validateApplicationStatus(Integer id, StatusType allowedStatus) {
    validateApplicationStatus(id, Collections.singletonList(allowedStatus));
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
      .anyMatch(t -> blocksDecisionMaking(t));
  }

  private boolean blocksDecisionMaking(ApplicationTagJson t) {
    return t.getType() == ApplicationTagType.DATE_CHANGE || t.getType() == ApplicationTagType.OTHER_CHANGES;
  }

}
