package fi.hel.allu.supervision.api.controller;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.servicecore.domain.CreateProjectJson;
import fi.hel.allu.servicecore.domain.ProjectJson;
import fi.hel.allu.servicecore.mapper.ProjectMapper;
import fi.hel.allu.servicecore.service.ProjectServiceComposer;
import fi.hel.allu.supervision.api.domain.ProjectSearchParameters;
import fi.hel.allu.supervision.api.domain.ProjectSearchResult;
import fi.hel.allu.supervision.api.mapper.MapperUtil;
import fi.hel.allu.supervision.api.mapper.ProjectSearchParameterMapper;
import fi.hel.allu.supervision.api.mapper.ProjectSearchResultMapper;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1/projects")
@Api(tags = "Projects")
public class ProjectController {

  @Autowired
  private ProjectServiceComposer projectServiceComposer;
  @Autowired
  private ProjectSearchParameterMapper searchParameterMapper;
  @Autowired
  private ProjectSearchResultMapper searchResultMapper;
  @Autowired
  private ProjectMapper projectMapper;


  @ApiOperation(value = "Search projects",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = ProjectSearchResult.class,
      responseContainer="List"
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Projects retrieved successfully", response = ProjectSearchResult.class, responseContainer="List"),
      @ApiResponse(code = 400, message = "Invalid search parameters", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/search", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Page<ProjectSearchResult>> search(@RequestBody @Valid ProjectSearchParameters searchParameters) {
    QueryParameters queryParameters = searchParameterMapper.mapToQueryParameters(searchParameters);
    Pageable pageRequest = MapperUtil.mapToPageRequest(searchParameters);
    return ResponseEntity.ok(projectServiceComposer.search(queryParameters, pageRequest).map(p -> searchResultMapper.mapToSearchResult(p)));
  }

  @ApiOperation(value = "Create new project. Generates default identifier if not given in input model. Returns ID of the created project.",
      authorizations = @Authorization(value ="api_key"),
      response = Integer.class
  )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Project created successfully", response = Integer.class),
  })
  @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Integer> create(@RequestBody @Valid CreateProjectJson project) {
    ProjectJson createdProject = projectServiceComposer.insert(projectMapper.mapCreateJsonToProjectJson(project));
    return ResponseEntity.ok(createdProject.getId());
  }

  @ApiOperation(value = "Get project by ID",
      authorizations = @Authorization(value ="api_key"),
      response = ProjectJson.class,
      produces = "application/json"
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Project fetched successfully", response = ProjectJson.class)
  })
  @RequestMapping(value = "/{id}",  method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<ProjectJson> findById(@PathVariable Integer id) {
    return ResponseEntity.ok(projectServiceComposer.findById(id));
  }

  @ApiOperation(value = "Remove project",
      authorizations = @Authorization(value ="api_key")
  )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Project deleted successfully")
  })
  @RequestMapping(value = "/{id}",  method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> remove(@PathVariable Integer id) {
    projectServiceComposer.delete(id);
    return ResponseEntity.ok().build();
  }
}
