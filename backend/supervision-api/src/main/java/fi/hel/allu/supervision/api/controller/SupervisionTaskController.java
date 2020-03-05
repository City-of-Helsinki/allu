package fi.hel.allu.supervision.api.controller;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.SupervisionTaskSearchCriteria;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.service.SupervisionTaskService;
import fi.hel.allu.supervision.api.domain.*;
import fi.hel.allu.supervision.api.mapper.MapperUtil;
import fi.hel.allu.supervision.api.mapper.SupervisionTaskMapper;
import fi.hel.allu.supervision.api.mapper.SupervisionTaskSearchParameterMapper;
import fi.hel.allu.supervision.api.service.SupervisionTaskApprovalService;
import fi.hel.allu.supervision.api.validation.SupervisionTaskApprovalValidator;
import fi.hel.allu.supervision.api.validation.SupervisionTaskModifyValidator;
import fi.hel.allu.supervision.api.validation.SupervisionTaskValidator;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1")
@Api(tags = "Supervision tasks")
public class SupervisionTaskController {

  @Autowired
  private SupervisionTaskService supervisionTaskService;
  @Autowired
  private SupervisionTaskSearchParameterMapper searchParameterMapper;
  @Autowired
  private SupervisionTaskMapper supervisionTaskMapper;
  @Autowired
  private SupervisionTaskValidator supervisionTaskValidator;
  @Autowired
  private SupervisionTaskApprovalValidator supervisionTaskApprovalValidator;
  @Autowired
  private SupervisionTaskModifyValidator supervisionTaskModifyValidator;
  @Autowired
  private SupervisionTaskApprovalService supervisionTaskApprovalService;

  @InitBinder("supervisionTaskCreateJson")
  protected void initCreateBinder(WebDataBinder binder) {
    binder.addValidators(supervisionTaskValidator, supervisionTaskModifyValidator);
  }

  @InitBinder("supervisionTaskModifyJson")
  protected void initUpdateBinder(WebDataBinder binder) {
    binder.addValidators(supervisionTaskModifyValidator);
  }

  @InitBinder("supervisionTaskApprovalJson")
  protected void initApprovalBinder(WebDataBinder binder) {
    binder.addValidators(supervisionTaskApprovalValidator);
  }

  @ApiOperation(value = "Search supervision tasks",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = SupervisionTaskSearchResult.class,
      responseContainer="List"
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Supervision tasks retrieved successfully", response = SupervisionTaskSearchResult.class, responseContainer="List"),
      @ApiResponse(code = 400, message = "Invalid search parameters", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/supervisiontasks/search", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
  public ResponseEntity<Page<SupervisionTaskSearchResult>> search(@RequestBody @Valid SupervisionTaskSearchParameters searchParameters) {
    SupervisionTaskSearchCriteria criteria = searchParameterMapper.createSearchCriteria(searchParameters);
    Pageable pageRequest = MapperUtil.mapToPageRequest(searchParameters);
    return ResponseEntity.ok(supervisionTaskService.search(criteria, pageRequest).map(s -> supervisionTaskMapper.mapToSearchResult(s)));
  }

  @ApiOperation(value = "Get supervision task by ID",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = SupervisionTaskSearchResult.class
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Supervision task retrieved successfully", response = SupervisionTaskSearchResult.class),
      @ApiResponse(code = 404, message = "Task with ID not found", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/supervisiontasks/{id}", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
  public ResponseEntity<SupervisionTaskSearchResult> findById(@PathVariable Integer id) {
    SupervisionTaskJson task = supervisionTaskService.findById(id);
    return ResponseEntity.ok(supervisionTaskMapper.mapToSearchResult(task));
  }

  @ApiOperation(value = "Get supervision tasks for application with given ID",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = SupervisionTaskSearchResult.class,
      responseContainer = "List"
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Supervision tasks retrieved successfully", response = SupervisionTaskSearchResult.class, responseContainer = "List"),
  })
  @RequestMapping(value = "/applications/{id}/supervisiontasks", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
  public ResponseEntity<List<SupervisionTaskSearchResult>> findByApplicationId(@PathVariable Integer id) {
    List<SupervisionTaskSearchResult> tasks = supervisionTaskService.findByApplicationId(id)
        .stream()
        .map(t -> supervisionTaskMapper.mapToSearchResult(t))
        .collect(Collectors.toList());
    return ResponseEntity.ok(tasks);
  }

  @ApiOperation(value = "Create new supervision task. Returns created task. "
      + "Creation is allowed for task types PRELIMINARY_SUPERVISION and SUPERVISION",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = Integer.class
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Supervision tasks created successfully", response = Integer.class),
      @ApiResponse(code = 400, message = "Invalid create parameters", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/supervisiontasks", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<SupervisionTaskSearchResult> create(@RequestBody @Valid SupervisionTaskCreateJson supervisionTask) {
    SupervisionTaskJson inserted = supervisionTaskService.insert(supervisionTaskMapper.mapToModel(supervisionTask));
    return ResponseEntity.ok(supervisionTaskMapper.mapToSearchResult(inserted));
  }

  @ApiOperation(value = "Update supervision task. Returns updated task. "
      + "Update is allowed for task with types PRELIMINARY_SUPERVISION and SUPERVISION.",
      authorizations = @Authorization(value = "api_key"),
      produces = "application/json",
      response = Integer.class
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Supervision tasks updated successfully", response = Integer.class),
      @ApiResponse(code = 400, message = "Invalid update parameters", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/supervisiontasks/{id}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<SupervisionTaskSearchResult> update(@PathVariable Integer id, @RequestBody @Valid SupervisionTaskModifyJson supervisionTask) {
    SupervisionTaskJson task = supervisionTaskService.findById(id);
    validateModificationAllowed(task);
    task.setPlannedFinishingTime(supervisionTask.getPlannedFinishingTime());
    task.setOwner(new UserJson(supervisionTask.getOwnerId()));
    task.setDescription(supervisionTask.getDescription());
    task = supervisionTaskService.update(task);
    return ResponseEntity.ok(supervisionTaskMapper.mapToSearchResult(task));
  }

  @ApiOperation(value = "Delete supervision task.  "
      + "Delete is allowed for task with types PRELIMINARY_SUPERVISION and SUPERVISION.",
      authorizations = @Authorization(value = "api_key")
  )
  @RequestMapping(value = "/supervisiontasks/{id}", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> delete(@PathVariable Integer id) {
    SupervisionTaskJson task = supervisionTaskService.findById(id);
    validateModificationAllowed(task);
    supervisionTaskService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "Approves supervision task and returns updated task.",
      notes =
            "<h3>Approval of operational condition supervision task</h3>"
          + " <ul>"
          + "   <li>Approval not allowed if there's no decision made for application (application status other than DECISION)</li>"
          + "   <li>Approval not allowed if application has DATE_CHANGE or OTHER_CHANGES tag</li>"
          + "   <li>Sets operational condition date for the excavation announcement according to given date</li>"
          + "   <li>Moves application to operational condition state if there's no changes in invoicing or in operational condition date after decision. If invoicing or operational condition date is changed, "
          + "   moves application to decision making state</li>"
          + " </ul>"
          + "<h3>Approval of final supervision task</h3>"
          + " <ul>"
          + "   <li>Approval not allowed if there's no decision made for application (application status other than DECISION or OPERATIONAL_CONDITION)</li>"
          + "   <li>Approval not allowed if application has DATE_CHANGE or OTHER_CHANGES tag</li>"
          + "   <li>Sets work finished date for the excavation announcement or area rental according to given date</li>"
          + "   <li>Moves application to finished state if there's no change in invoicing and work finished date is equal to original application end time. If invoicing or date is changed, "
          + "   moves application to decision making state</li>"
          + " </ul>",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = Integer.class
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Supervision task approved successfully", response = Integer.class),
      @ApiResponse(code = 400, message = "Invalid parameters", response = ErrorInfo.class),
      @ApiResponse(code = 403, message = "Approval of task not allowed", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/supervisiontasks/{id}/approved", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<SupervisionTaskSearchResult> approve(@PathVariable Integer id, @RequestBody @Valid SupervisionTaskApprovalJson approvalData) {
    return ResponseEntity.ok(supervisionTaskMapper.mapToSearchResult(supervisionTaskApprovalService.approveSupervisionTask(approvalData)));
  }

  @ApiOperation(value = "Rejects supervision task and creates a new task with given date.",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = SupervisionTaskSearchResult.class
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Supervision task rejected successfully", response = SupervisionTaskSearchResult.class),
      @ApiResponse(code = 400, message = "Invalid parameters", response = ErrorInfo.class),
  })
  @RequestMapping(value = "/supervisiontasks/{id}/rejected", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<SupervisionTaskSearchResult> reject(@PathVariable Integer id, @RequestBody @Valid SupervisionTaskRejectionJson rejectionData) {
    return ResponseEntity.ok(supervisionTaskMapper.mapToSearchResult(supervisionTaskApprovalService.rejectSupervisionTask(id, rejectionData)));
  }

  @ApiOperation(value = "Change owner of the supervision task. Returns updated task.",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = SupervisionTaskSearchResult.class
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Owner updated successfully", response = SupervisionTaskSearchResult.class),
  })
  @RequestMapping(value = "/supervisiontasks/{id}/ownerId", method = RequestMethod.PUT, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<SupervisionTaskSearchResult> updateOwner(@PathVariable Integer id, @ApiParam(value = "Id of the new owner") @RequestParam Integer ownerId) {
    supervisionTaskService.updateOwner(ownerId, Collections.singletonList(id));
    return ResponseEntity.ok(supervisionTaskMapper.mapToSearchResult(supervisionTaskService.findById(id)));
  }

  private void validateModificationAllowed(SupervisionTaskJson task) {
    if (task.getType() != SupervisionTaskType.PRELIMINARY_SUPERVISION &&
        task.getType() != SupervisionTaskType.SUPERVISION) {
      throw new IllegalOperationException("supervisiontask.modify.forbidden");
    }
  }
}
