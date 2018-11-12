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

import fi.hel.allu.common.domain.SupervisionTaskSearchCriteria;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.servicecore.service.SupervisionTaskService;
import fi.hel.allu.supervision.api.domain.SupervisionTaskSearchParameters;
import fi.hel.allu.supervision.api.domain.SupervisionTaskSearchResult;
import fi.hel.allu.supervision.api.mapper.MapperUtil;
import fi.hel.allu.supervision.api.mapper.SupervisionTaskSearchParameterMapper;
import fi.hel.allu.supervision.api.mapper.SupervisionTaskSearchResultMapper;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1/supervisiontasks")
@Api(value = "v1/supervisiontasks")
public class SupervisionTaskController {

  @Autowired
  private SupervisionTaskService supervisionTaskService;
  @Autowired
  private SupervisionTaskSearchParameterMapper searchParameterMapper;
  @Autowired
  private SupervisionTaskSearchResultMapper supervisionTaskMapper;


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
  @RequestMapping(value = "/search", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Page<SupervisionTaskSearchResult>> search(@RequestBody @Valid SupervisionTaskSearchParameters searchParameters) {
    SupervisionTaskSearchCriteria criteria = searchParameterMapper.createSearchCriteria(searchParameters);
    Pageable pageRequest = MapperUtil.mapToPageRequest(searchParameters);
    return ResponseEntity.ok(supervisionTaskService.search(criteria, pageRequest).map(s -> supervisionTaskMapper.mapToSearchResult(s)));
  }
}
