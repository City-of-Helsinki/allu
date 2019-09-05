package fi.hel.allu.supervision.api.controller;


import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.service.CustomerService;
import fi.hel.allu.supervision.api.domain.CustomerSearchParameters;
import fi.hel.allu.supervision.api.domain.CustomerSearchResult;
import fi.hel.allu.supervision.api.mapper.CustomerSearchParameterMapper;
import fi.hel.allu.supervision.api.mapper.CustomerSearchResultMapper;
import fi.hel.allu.supervision.api.mapper.MapperUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@Api(tags = "Customers")
public class CustomerController {

  @Autowired
  private CustomerService customerService;

  @Autowired
  private CustomerSearchResultMapper customerSearchResultMapper;

  @ApiOperation(value = "Search customers",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = CustomerSearchResult.class,
      responseContainer="List"
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Customers retrieved successfully", response = CustomerSearchResult.class, responseContainer="List"),
      @ApiResponse(code = 400, message = "Invalid search parameters", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/customers/search", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Page<CustomerSearchResult>> search(@RequestBody @Valid CustomerSearchParameters customerSearchParameters) {
    QueryParameters queryParameters = CustomerSearchParameterMapper.mapToQueryParameters(customerSearchParameters);
    Pageable pageable = MapperUtil.mapToPageRequest(customerSearchParameters);
    Page<CustomerJson> result = customerService.search(queryParameters, pageable);
    Page<CustomerSearchResult> response = result.map(a -> customerSearchResultMapper.mapToSearchResult(a));
    return ResponseEntity.ok(response);
  }

  @ApiOperation(value = "Create a new customer",
    authorizations = @Authorization(value = "api_key"),
    consumes = "application/json",
    produces = "application/json",
    response = CustomerSearchResult.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Customer created successfully", response = CustomerSearchResult.class),
    @ApiResponse(code = 400, message = "Invalid customer data", response = ErrorInfo.class),
    @ApiResponse(code = 403, message = "Customer addition forbidden", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/customers", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<CustomerSearchResult> create(@RequestBody @Valid CustomerJson customerJson) {
    CustomerJson createdCustomer = customerService.createCustomer(customerJson);
    return ResponseEntity.ok(customerSearchResultMapper.mapToSearchResult(createdCustomer));
  }
}
