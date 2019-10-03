package fi.hel.allu.supervision.api.controller;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.search.domain.QueryParameters;
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
}
