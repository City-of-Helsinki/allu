package fi.hel.allu.supervision.api.controller;


import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.servicecore.domain.search.ApplicationSearchResult;
import fi.hel.allu.servicecore.domain.search.LocationSearchResult;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.supervision.api.domain.SearchParameterField;
import fi.hel.allu.supervision.api.domain.SearchParameterType;
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
    ApplicationQueryParameters queryParameters = mapToQueryParameters(searchParameters);
    Pageable pageable = mapToPageRequest(searchParameters);
    Page<ApplicationES> result = applicationServiceComposer.search(queryParameters, pageable, Boolean.FALSE);
    Page<ApplicationSearchResult> response = result.map(a -> applicationMapper.mapToSearchResult(a));
    return ResponseEntity.ok(response);
  }
}
