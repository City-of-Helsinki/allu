package fi.hel.allu.supervision.api.controller;


import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ApplicationQueryParameters;
import fi.hel.allu.servicecore.domain.search.ApplicationSearchResult;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.supervision.api.domain.SearchParameters;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1/applications")
@Api(value = "v1/applications")
public class ApplicationController {

  DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;
  @Autowired
  private ApplicationMapper applicationMapper;

  @ApiOperation(value = "Search applications",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = ApplicationES.class,
      responseContainer="List"
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Applications retrieved successfully", response = ApplicationSearchResult.class, responseContainer="List")
  })
  @RequestMapping(value = "/search", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Page<ApplicationSearchResult>> search(@RequestBody SearchParameters searchParameters) {
    ApplicationQueryParameters queryParameters = SearchParameterMapper.mapToQueryParameters(searchParameters);
    Pageable pageable = SearchParameterMapper.mapToPageRequest(searchParameters);
    Page<ApplicationES> result = applicationServiceComposer.search(queryParameters, pageable, Boolean.FALSE);
    Page<ApplicationSearchResult> response = result.map(a -> applicationMapper.mapToSearchResult(a));
    return ResponseEntity.ok(response);
  }
}
