package fi.hel.allu.supervision.api.controller;


import java.util.List;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ApplicationQueryParameters;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.supervision.api.domain.ApplicationSearchParameters;
import fi.hel.allu.supervision.api.domain.ApplicationSearchResult;
import fi.hel.allu.supervision.api.mapper.ApplicationSearchParameterMapper;
import fi.hel.allu.supervision.api.mapper.ApplicationSearchResultMapper;
import fi.hel.allu.supervision.api.mapper.MapperUtil;

@RestController
@RequestMapping("/v1")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Applications")
public class ApplicationSearchController {

  private final ApplicationServiceComposer applicationServiceComposer;
  private final ApplicationSearchResultMapper applicationMapper;

  public ApplicationSearchController(ApplicationServiceComposer applicationServiceComposer,
                                     ApplicationSearchResultMapper applicationMapper) {
    this.applicationServiceComposer = applicationServiceComposer;
    this.applicationMapper = applicationMapper;
  }

  @Operation(summary = "Search applications")
  @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Applications retrieved successfully",
              content = @Content(schema = @Schema(implementation = ApplicationSearchResult.class))),
      @ApiResponse(responseCode = "400", description = "Invalid search parameters",
              content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
  })
  @PostMapping(value = "/applications/search", produces = "application/json", consumes = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
  public ResponseEntity<Page<ApplicationSearchResult>> search(@RequestBody @Valid ApplicationSearchParameters searchParameters) {
    ApplicationQueryParameters queryParameters = ApplicationSearchParameterMapper.mapToQueryParameters(searchParameters);
    Pageable pageable = MapperUtil.mapToPageRequest(searchParameters);
    Page<ApplicationES> result = applicationServiceComposer.search(queryParameters, pageable, Boolean.FALSE);
    Page<ApplicationSearchResult> response = result.map(applicationMapper::mapToSearchResult);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Get applications of project")
  @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Applications retrieved successfully",
              content = @Content(schema = @Schema(implementation = ApplicationSearchResult.class))),
  })
  @GetMapping(value = "/projects/{projectId}/applications", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
  public ResponseEntity<List<ApplicationSearchResult>> getProjectApplications(@PathVariable Integer projectId) {
    ApplicationQueryParameters queryParameters = ApplicationSearchParameterMapper.queryParametersForProject(projectId);
    Page<ApplicationES> result = applicationServiceComposer.search(queryParameters, MapperUtil.DEFAULT_PAGE_REQUEST, Boolean.FALSE);
    Page<ApplicationSearchResult> response = result.map(applicationMapper::mapToSearchResult);
    return ResponseEntity.ok(response.getContent());
  }


}
