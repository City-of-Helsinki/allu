package fi.hel.allu.supervision.api.controller;


import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.service.SupervisionTaskService;
import fi.hel.allu.supervision.api.domain.*;
import fi.hel.allu.supervision.api.mapper.SupervisionTaskMapper;
import fi.hel.allu.supervision.api.mapper.SupervisionTaskSearchParameterMapper;
import fi.hel.allu.supervision.api.service.SupervisionTaskApprovalService;
import fi.hel.allu.supervision.api.validation.SupervisionTaskApprovalValidator;
import fi.hel.allu.supervision.api.validation.SupervisionTaskModifyValidator;
import fi.hel.allu.supervision.api.validation.SupervisionTaskValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Supervision tasks")
public class SupervisionTaskController {

    private final SupervisionTaskService supervisionTaskService;
    private final SupervisionTaskSearchParameterMapper searchParameterMapper;
    private final SupervisionTaskMapper supervisionTaskMapper;
    private final SupervisionTaskValidator supervisionTaskValidator;
    private final SupervisionTaskApprovalValidator supervisionTaskApprovalValidator;
    private final SupervisionTaskModifyValidator supervisionTaskModifyValidator;
    private final SupervisionTaskApprovalService supervisionTaskApprovalService;

    public SupervisionTaskController(SupervisionTaskService supervisionTaskService,
                                     SupervisionTaskSearchParameterMapper searchParameterMapper,
                                     SupervisionTaskMapper supervisionTaskMapper,
                                     SupervisionTaskValidator supervisionTaskValidator,
                                     SupervisionTaskApprovalValidator supervisionTaskApprovalValidator,
                                     SupervisionTaskModifyValidator supervisionTaskModifyValidator,
                                     SupervisionTaskApprovalService supervisionTaskApprovalService) {
        this.supervisionTaskService = supervisionTaskService;
        this.searchParameterMapper = searchParameterMapper;
        this.supervisionTaskMapper = supervisionTaskMapper;
        this.supervisionTaskValidator = supervisionTaskValidator;
        this.supervisionTaskApprovalValidator = supervisionTaskApprovalValidator;
        this.supervisionTaskModifyValidator = supervisionTaskModifyValidator;
        this.supervisionTaskApprovalService = supervisionTaskApprovalService;
    }

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

    @Operation(summary = "Search supervision tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supervision tasks retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SupervisionTaskSearchResult.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PostMapping(value = "/supervisiontasks/search", produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Page<SupervisionTaskSearchResult>> search(
            @RequestBody @Valid QueryParameters queryParameters,
            @PageableDefault(page = 0, size = 100, sort = "id", direction = Sort.Direction.DESC) Pageable pageRequest) {
        return ResponseEntity.ok(supervisionTaskService.search(queryParameters, pageRequest).map(
                supervisionTaskMapper::mapToSearchResult));
    }

    @Operation(summary = "Get supervision task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supervision task retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SupervisionTaskSearchResult.class))),
            @ApiResponse(responseCode = "404", description = "Task with ID not found",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping(value = "/supervisiontasks/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<SupervisionTaskSearchResult> findById(@PathVariable Integer id) {
        SupervisionTaskJson task = supervisionTaskService.findById(id);
        return ResponseEntity.ok(supervisionTaskMapper.mapToSearchResult(task));
    }

    @Operation(summary = "Get supervision tasks for application with given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supervision tasks retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SupervisionTaskSearchResult.class))),
    })
    @GetMapping(value = "/applications/{id}/supervisiontasks", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<List<SupervisionTaskSearchResult>> findByApplicationId(@PathVariable Integer id) {
        List<SupervisionTaskSearchResult> tasks = supervisionTaskService.findByApplicationId(id)
                .stream()
                .map(supervisionTaskMapper::mapToSearchResult)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Create new supervision task. Returns created task. "
            + "Creation is allowed for task types PRELIMINARY_SUPERVISION and SUPERVISION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supervision tasks created successfully",
                    content = @Content(schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "400", description = "Invalid create parameters",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PostMapping(value = "/supervisiontasks", produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<SupervisionTaskSearchResult> create(
            @RequestBody @Valid SupervisionTaskCreateJson supervisionTask) {
        SupervisionTaskJson inserted =
                supervisionTaskService.insert(supervisionTaskMapper.mapToModel(supervisionTask));
        return ResponseEntity.ok(supervisionTaskMapper.mapToSearchResult(inserted));
    }

    @Operation(summary = "Update supervision task. Returns updated task. "
            + "Update is allowed for task with types PRELIMINARY_SUPERVISION and SUPERVISION.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supervision tasks updated successfully",
                    content = @Content(schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update parameters",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PutMapping(value = "/supervisiontasks/{id}", produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<SupervisionTaskSearchResult> update(@PathVariable Integer id,
                                                              @RequestBody
                                                              @Valid SupervisionTaskModifyJson supervisionTask) {
        SupervisionTaskJson task = supervisionTaskService.findById(id);
        validateModificationAllowed(task);
        task.setPlannedFinishingTime(supervisionTask.getPlannedFinishingTime());
        task.setOwner(new UserJson(supervisionTask.getOwnerId()));
        task.setDescription(supervisionTask.getDescription());
        task = supervisionTaskService.update(task);
        return ResponseEntity.ok(supervisionTaskMapper.mapToSearchResult(task));
    }

    @Operation(summary = "Delete supervision task.  "
            + "Delete is allowed for task with types PRELIMINARY_SUPERVISION and SUPERVISION.")
    @DeleteMapping(value = "/supervisiontasks/{id}")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        SupervisionTaskJson task = supervisionTaskService.findById(id);
        validateModificationAllowed(task);
        supervisionTaskService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Approves supervision task and returns updated task.",
            description =
                    "<h3>Approval of operational condition supervision task</h3>"
                            + " <ul>"
                            + "   <li>Approval not allowed in states PENDING_CLIENT, PRE_RESERVED, PENDING, " +
							"WAITING_INFORMATION, "
                            + "   INFORMATION_RECEIVED, HANDLING, RETURNED_TO_PREPARATION, WAITING_CONTRACT_APPROVAL," +
							" " +
                            "DECISIONMAKING, and REPLACED</li>"
                            + "   <li>Approval not allowed if application has DATE_CHANGE or OTHER_CHANGES tag</li>"
                            + "   <li>Sets operational condition date for the excavation announcement according to " +
							"given date</li>"
                            + "   <li>Moves application to operational condition state if there's no changes in " +
							"invoicing or in " +
                            "operational condition date after decision. If invoicing or operational condition date is" +
							" changed, "
                            + "   moves application to decision making state</li>"
                            + " </ul>"
                            + "<h3>Approval of final supervision task</h3>"
                            + " <ul>"
                            + "   <li>Approval not allowed if there's no decision made for application (application " +
							"status other " +
                            "than DECISION or OPERATIONAL_CONDITION)</li>"
                            + "   <li>Approval not allowed if application has DATE_CHANGE or OTHER_CHANGES tag</li>"
                            + "   <li>Sets work finished date for the excavation announcement or area rental " +
							"according to given " +
                            "date</li>"
                            + "   <li>Moves application to finished state if there's no change in invoicing and work " +
							"finished date " +
                            "is equal to original application end time. If invoicing or date is changed, "
                            + "   moves application to decision making state</li>"
                            + " </ul>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supervision task approved successfully",
                    content = @Content(schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "403", description = "Approval of task not allowed",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PutMapping(value = "/supervisiontasks/{id}/approved", produces = "application/json", consumes = "application" +
            "/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<SupervisionTaskSearchResult> approve(@PathVariable Integer id,
                                                               @RequestBody
                                                               @Valid SupervisionTaskApprovalJson approvalData) {
        return ResponseEntity.ok(
                supervisionTaskMapper.mapToSearchResult(
                        supervisionTaskApprovalService.approveSupervisionTask(approvalData)));
    }

    @Operation(summary = "Rejects supervision task and creates a new task with given date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supervision task rejected successfully",
                    content = @Content(schema = @Schema(implementation = SupervisionTaskSearchResult.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
    })
    @PutMapping(value = "/supervisiontasks/{id}/rejected", produces = "application/json", consumes = "application" +
            "/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<SupervisionTaskSearchResult> reject(@PathVariable Integer id,
                                                              @RequestBody
                                                              @Valid SupervisionTaskRejectionJson rejectionData) {
        return ResponseEntity.ok(supervisionTaskMapper.mapToSearchResult(
                supervisionTaskApprovalService.rejectSupervisionTask(id, rejectionData)));
    }

    @Operation(summary = "Change owner of the supervision task. Returns updated task.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Owner updated successfully",
                    content = @Content(schema = @Schema(implementation = SupervisionTaskSearchResult.class))),
    })
    @PutMapping(value = "/supervisiontasks/{id}/ownerId", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<SupervisionTaskSearchResult> updateOwner(@PathVariable Integer id, @Parameter(
            description = "Id of the new owner") @RequestParam Integer ownerId) {
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