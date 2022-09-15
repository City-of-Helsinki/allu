package fi.hel.allu.supervision.api.controller;


import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.servicecore.domain.ModifyProjectJson;
import fi.hel.allu.servicecore.domain.ProjectJson;
import fi.hel.allu.servicecore.mapper.ProjectMapper;
import fi.hel.allu.servicecore.service.ProjectService;
import fi.hel.allu.servicecore.service.ProjectServiceComposer;
import fi.hel.allu.supervision.api.domain.ProjectSearchParameters;
import fi.hel.allu.supervision.api.domain.ProjectSearchResult;
import fi.hel.allu.supervision.api.mapper.MapperUtil;
import fi.hel.allu.supervision.api.mapper.ProjectSearchParameterMapper;
import fi.hel.allu.supervision.api.mapper.ProjectSearchResultMapper;
import fi.hel.allu.supervision.api.validation.ProjectCustomerValidator;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/projects")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Projects")
public class ProjectController {

    private final ProjectServiceComposer projectServiceComposer;
    private final ProjectService projectService;
    private final ProjectSearchParameterMapper searchParameterMapper;
    private final ProjectSearchResultMapper searchResultMapper;
    private final ProjectMapper projectMapper;
    private final ProjectCustomerValidator projectCustomerValidator;

    public ProjectController(ProjectServiceComposer projectServiceComposer, ProjectService projectService,
                             ProjectSearchParameterMapper searchParameterMapper,
                             ProjectSearchResultMapper searchResultMapper, ProjectMapper projectMapper,
                             ProjectCustomerValidator projectCustomerValidator) {
        this.projectServiceComposer = projectServiceComposer;
        this.projectService = projectService;
        this.searchParameterMapper = searchParameterMapper;
        this.searchResultMapper = searchResultMapper;
        this.projectMapper = projectMapper;
        this.projectCustomerValidator = projectCustomerValidator;
    }

    @InitBinder("modifyProjectJson")
    protected void initUpdateBinder(WebDataBinder binder) {
        binder.addValidators(projectCustomerValidator);
    }

    @Operation(summary = "Search projects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projects retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProjectSearchResult.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PostMapping(value = "/search", produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Page<ProjectSearchResult>> search(
            @RequestBody @Valid ProjectSearchParameters searchParameters) {
        QueryParameters queryParameters = searchParameterMapper.mapToQueryParameters(searchParameters);
        Pageable pageRequest = MapperUtil.mapToPageRequest(searchParameters);
        return ResponseEntity.ok(projectServiceComposer.search(queryParameters, pageRequest).map(
                searchResultMapper::mapToSearchResult));
    }

    @Operation(summary = "Create new project. Generates default identifier if not given in input model. " +
            "Returns ID of the created project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project created successfully",
                    content = @Content(schema = @Schema(implementation = Integer.class)))
    })
    @PostMapping(consumes = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<Integer> create(@RequestBody @Valid ModifyProjectJson project) {
        ProjectJson createdProject = projectServiceComposer.insert(projectMapper.mapCreateJsonToProjectJson(project));
        return ResponseEntity.ok(createdProject.getId());
    }

    @Operation(summary = "Get project by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project fetched successfully",
                    content = @Content(schema = @Schema(implementation = ProjectJson.class)))
    })
    @GetMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<ProjectJson> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(projectServiceComposer.findById(id));
    }

    @Operation(summary = "Remove project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project deleted successfully")
    })
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<Void> remove(@PathVariable Integer id) {
        projectServiceComposer.delete(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Add applications to project. If application already is in some other project, it's moved to " +
					"this " +
                    "project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Applications added successfully")
    })
    @PutMapping(value = "/{id}/applications")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<Void> addApplications(@PathVariable int id,
                                                @Parameter(
                                                        description = "Ids of the applications to be added")
                                                @RequestBody List<Integer> applicationIds) {
        projectServiceComposer.addApplications(id, applicationIds);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove application from project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application removed successfully")
    })
    @DeleteMapping(value = "/{id}/applications/{applicationId}")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<Void> removeApplication(@PathVariable int id,
                                                  @Parameter(
                                                          description = "Id of the application to be remove")
                                                  @PathVariable Integer applicationId) {
        if (!projectServiceComposer.projectHasApplication(id, applicationId)) {
            throw new IllegalOperationException("project.application.notfound");
        }
        projectServiceComposer.removeApplication(applicationId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update project. ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project updated successfully",
                    content = @Content(schema = @Schema(implementation = ProjectJson.class)))
    })
    @PutMapping(value = "/{id}", consumes = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<ProjectJson> update(@PathVariable int id,
                                              @Parameter(
                                                      description = "Updated project data") @RequestBody
                                              @Valid ModifyProjectJson project) {
        return ResponseEntity.ok(projectServiceComposer.update(id, project));
    }

    @Operation(summary = "Add child projects for project. If child project is currently child of some other project, " +
            "parent of the child project is updated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Child projects added successfully")
    })
    @PutMapping(value = "/{id}/childProjects")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<Void> addChildProjects(@PathVariable int id,
                                                 @Parameter(
                                                         description = "Ids of the projects to be added") @RequestBody
                                                 List<Integer> childProjectIds) {
        childProjectIds.forEach(childId -> projectServiceComposer.updateProjectParent(childId, id));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "List child projects of the project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projects listed successfully",
                    content = @Content(schema = @Schema(implementation = ProjectJson.class)))
    })
    @GetMapping(value = "/{id}/childProjects", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<List<ProjectJson>> getChildProjects(@PathVariable int id) {
        return ResponseEntity.ok(projectService.findProjectChildren(id));
    }

    @Operation(
            summary = "Remove child project from project. Removes only parent-child relationship, does not delete child " +
                    "project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Child project removed successfully")
    })
    @DeleteMapping(value = "/{id}/childProjects/{childProjectId}")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<Void> removeChildProject(
            @PathVariable(value = "id") @Parameter(description = "ID of the parent project") int id,
            @PathVariable(value = "childProjectId") @Parameter(
                    description = "ID of the child project to remove") int childProjectId) {
        if (projectService.findProjectChildren(id).stream().noneMatch(c -> c.getId().equals(childProjectId))) {
            throw new IllegalArgumentException("project.childproject.notfound");
        }
        projectServiceComposer.updateProjectParent(childProjectId, null);
        return ResponseEntity.ok().build();
    }
}
