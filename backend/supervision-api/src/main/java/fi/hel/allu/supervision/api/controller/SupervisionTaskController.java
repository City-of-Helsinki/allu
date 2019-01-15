package fi.hel.allu.supervision.api.controller;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.SupervisionTaskSearchCriteria;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.servicecore.service.SupervisionTaskService;
import fi.hel.allu.supervision.api.domain.SupervisionTaskCreateJson;
import fi.hel.allu.supervision.api.domain.SupervisionTaskSearchParameters;
import fi.hel.allu.supervision.api.domain.SupervisionTaskSearchResult;
import fi.hel.allu.supervision.api.mapper.MapperUtil;
import fi.hel.allu.supervision.api.mapper.SupervisionTaskMapper;
import fi.hel.allu.supervision.api.mapper.SupervisionTaskSearchParameterMapper;
import fi.hel.allu.supervision.api.validation.SupervisionTaskValidator;
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
  private SupervisionTaskMapper supervisionTaskMapper;
  @Autowired
  private SupervisionTaskValidator supervisionTaskValidator;


  @InitBinder("supervisionTaskCreateJson")
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(supervisionTaskValidator);
  }

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

  @ApiOperation(value = "Create new supervision task. Returns ID of the created task. "
      + "Creation is allowed for task types PRELIMINARY_SUPERVISION and SUPERVISION",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = Integer.class
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Supervision tasks created successfully", response = Integer.class),
      @ApiResponse(code = 400, message = "Invalid create parameters", response = ErrorInfo.class)
  })
  @RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Integer> create(@RequestBody @Valid SupervisionTaskCreateJson supervisionTask) {
    return ResponseEntity.ok(supervisionTaskService.insert(supervisionTaskMapper.mapToModel(supervisionTask)).getId());
  }

}
