package fi.hel.allu.supervision.api.controller;


import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.model.domain.AreaRental;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ApplicationQueryParameters;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.supervision.api.domain.ApplicationSearchParameters;
import fi.hel.allu.supervision.api.domain.ApplicationSearchResult;
import fi.hel.allu.supervision.api.domain.AreaRentalApplication;
import fi.hel.allu.supervision.api.domain.ExcavationAnnouncementApplication;
import fi.hel.allu.supervision.api.mapper.ApplicationSearchParameterMapper;
import fi.hel.allu.supervision.api.mapper.ApplicationSearchResultMapper;
import fi.hel.allu.supervision.api.mapper.MapperUtil;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1")
@Api
public class ApplicationController {

  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;
  @Autowired
  private ApplicationSearchResultMapper applicationMapper;

  @ApiOperation(value = "Search applications",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = ApplicationSearchResult.class,
      responseContainer="List"
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Applications retrieved successfully", response = ApplicationSearchResult.class, responseContainer="List"),
      @ApiResponse(code = 400, message = "Invalid search parameters", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/applications/search", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Page<ApplicationSearchResult>> search(@RequestBody @Valid ApplicationSearchParameters searchParameters) {
    ApplicationQueryParameters queryParameters = ApplicationSearchParameterMapper.mapToQueryParameters(searchParameters);
    Pageable pageable = MapperUtil.mapToPageRequest(searchParameters);
    Page<ApplicationES> result = applicationServiceComposer.search(queryParameters, pageable, Boolean.FALSE);
    Page<ApplicationSearchResult> response = result.map(a -> applicationMapper.mapToSearchResult(a));
    return ResponseEntity.ok(response);
  }

  @ApiOperation(value = "Get applications of project",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = ApplicationSearchResult.class,
      responseContainer="List"
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Applications retrieved successfully", response = ApplicationSearchResult.class, responseContainer="List"),
  })
  @RequestMapping(value = "/projects/{projectId}/applications", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<List<ApplicationSearchResult>> getProjectApplications(@PathVariable Integer projectId) {
    ApplicationQueryParameters queryParameters = ApplicationSearchParameterMapper.queryParametersForProject(projectId);
    Page<ApplicationES> result = applicationServiceComposer.search(queryParameters, MapperUtil.DEFAULT_PAGE_REQUEST, Boolean.FALSE);
    Page<ApplicationSearchResult> response = result.map(a -> applicationMapper.mapToSearchResult(a));
    return ResponseEntity.ok(response.getContent());
  }


  @ApiOperation(value = "Get excavation announcement application details",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = ExcavationAnnouncementApplication.class
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Application retrieved successfully", response = ExcavationAnnouncementApplication.class),
  })
  @RequestMapping(value = "/excavationannouncements/{id}", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<ExcavationAnnouncementApplication> getExcavationAnnouncement(@PathVariable Integer id) {
    ApplicationJson application = applicationServiceComposer.findApplicationById(id);
    validateType(application, ApplicationType.EXCAVATION_ANNOUNCEMENT);
    return ResponseEntity.ok(new ExcavationAnnouncementApplication(application));
  }

  @ApiOperation(value = "Get area rental application details",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = AreaRentalApplication.class
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Application retrieved successfully", response = AreaRentalApplication.class),
  })
  @RequestMapping(value = "/arearentals/{id}", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<AreaRentalApplication> getAreaRental(@PathVariable Integer id) {
    ApplicationJson application = applicationServiceComposer.findApplicationById(id);
    validateType(application, ApplicationType.AREA_RENTAL);
    return ResponseEntity.ok(new AreaRentalApplication(application));
  }

  private void validateType(ApplicationJson application, ApplicationType expectedType) {
    if (application.getType() != expectedType) {
      throw new IllegalOperationException("applicationtype.invalid");
    }
  }
}
