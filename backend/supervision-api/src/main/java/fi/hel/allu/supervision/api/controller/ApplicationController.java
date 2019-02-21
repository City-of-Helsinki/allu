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
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ApplicationQueryParameters;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.supervision.api.domain.*;
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

  @ApiOperation(value = "Get cable report application details",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = CableReportApplication.class
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Application retrieved successfully", response = CableReportApplication.class),
  })
  @RequestMapping(value = "/cablereports/{id}", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<CableReportApplication> getCableReport(@PathVariable Integer id) {
    ApplicationJson application = applicationServiceComposer.findApplicationById(id);
    validateType(application, ApplicationType.CABLE_REPORT);
    return ResponseEntity.ok(new CableReportApplication(application));
  }


  @ApiOperation(value = "Update area rental application. "
      + "Update is allowed only if the status of the application is PENDING and application is not an external application "
      + "or status of the application is HANDLING, PRE_RESERVED or RETURNED_TO_PREPARATION.",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = AreaRentalApplication.class
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Application updated successfully", response = AreaRentalApplication.class),
      @ApiResponse(code = 409, message = "Update failed, given version of application updated by another user", response = ErrorInfo.class),
      @ApiResponse(code = 403, message = "Application update forbidden", response = ErrorInfo.class),

  })
  @RequestMapping(value = "/arearentals/{id}", method = RequestMethod.PUT, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<AreaRentalApplication> updateAreaRentalApplication(@PathVariable Integer id, @RequestBody @Valid AreaRentalApplication areaRentalApplication) {
    ApplicationJson updatedApplication = updateApplication(id, areaRentalApplication);
    return ResponseEntity.ok(new AreaRentalApplication(updatedApplication));
  }

  @ApiOperation(value = "Update excavation announcement application. "
      + "Update is allowed only if the status of the application is PENDING and application is not an external application "
      + "or status of the application is HANDLING, PRE_RESERVED or RETURNED_TO_PREPARATION.",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = ExcavationAnnouncementApplication.class
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Application updated successfully", response = ExcavationAnnouncementApplication.class),
      @ApiResponse(code = 409, message = "Update failed, given version of application updated by another user", response = ErrorInfo.class),
      @ApiResponse(code = 403, message = "Application update forbidden", response = ErrorInfo.class),
  })
  @RequestMapping(value = "/excavationannouncements/{id}", method = RequestMethod.PUT, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<ExcavationAnnouncementApplication> updateExcavationAnnouncementApplication(
      @PathVariable Integer id,
      @RequestBody @Valid ExcavationAnnouncementApplication excavationAnnouncementApplication) {
    ApplicationJson updatedApplication = updateApplication(id, excavationAnnouncementApplication);
    return ResponseEntity.ok(new ExcavationAnnouncementApplication(updatedApplication));
  }

  @ApiOperation(value = "Update cable report application. "
      + "Update is allowed only if the status of the application is PENDING and application is not an external application "
      + "or status of the application is HANDLING, PRE_RESERVED or RETURNED_TO_PREPARATION.",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = CableReportApplication.class
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Application updated successfully", response = CableReportApplication.class),
      @ApiResponse(code = 409, message = "Update failed, given version of application updated by another user", response = ErrorInfo.class),
      @ApiResponse(code = 403, message = "Application update forbidden", response = ErrorInfo.class),
  })
  @RequestMapping(value = "/cablereports/{id}", method = RequestMethod.PUT, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<CableReportApplication> updateCableReportApplication(@PathVariable Integer id, @RequestBody @Valid CableReportApplication cableReportApplication) {
    ApplicationJson updatedApplication = updateApplication(id, cableReportApplication);
    return ResponseEntity.ok(new CableReportApplication(updatedApplication));
  }

  private <T extends BaseApplication<?>> ApplicationJson updateApplication(Integer id, T application) {
    ApplicationJson applicationJson = application.getApplication();
    applicationJson.setExtension(application.getExtension());
    validateUpdateAllowed(id);
    return applicationServiceComposer.updateApplication(id, applicationJson);
  }

  private void validateUpdateAllowed(Integer id) {
    StatusType status = applicationServiceComposer.getApplicationStatus(id).getStatus();
    if (status != StatusType.HANDLING
        && status != StatusType.PRE_RESERVED
        && status != StatusType.RETURNED_TO_PREPARATION
        && (status != StatusType.PENDING || isExternalApplication(id))) {
      throw new IllegalArgumentException("application.applicationStatus.forbidden");
    }
  }

  private boolean isExternalApplication(Integer id) {
    return applicationServiceComposer.getApplicationExternalOwner(id) != null;
  }

  private void validateType(ApplicationJson application, ApplicationType expectedType) {
    if (application.getType() != expectedType) {
      throw new IllegalOperationException("applicationtype.invalid");
    }
  }
}
