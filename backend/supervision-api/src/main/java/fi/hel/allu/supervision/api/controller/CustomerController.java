package fi.hel.allu.supervision.api.controller;


import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.servicecore.domain.ContactJson;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.service.ContactService;
import fi.hel.allu.servicecore.service.CustomerService;
import fi.hel.allu.supervision.api.domain.CustomerSearchParameters;
import fi.hel.allu.supervision.api.domain.CustomerSearchResult;
import fi.hel.allu.supervision.api.mapper.CustomerSearchParameterMapper;
import fi.hel.allu.supervision.api.mapper.CustomerSearchResultMapper;
import fi.hel.allu.supervision.api.mapper.MapperUtil;
import fi.hel.allu.supervision.api.service.CustomerUpdateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1")
@Api(tags = "Customers")
public class CustomerController {

  @Autowired
  private CustomerService customerService;

  @Autowired
  private CustomerSearchResultMapper customerSearchResultMapper;

  @Autowired
  private CustomerUpdateService customerUpdateService;

  @Autowired
  private ContactService contactService;

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

  @ApiOperation(value = "Update a customer",
    notes =
      "<p>Data is given as key/value pair updated field being the key and it's new value (as JSON) the value. "
        + "All fields that are not marked as read only can be updated through this API.</p>",
    authorizations = @Authorization(value ="api_key"),
    produces = "application/json"
  )
  @ApiResponses( value = {
    @ApiResponse(code = 200, message = "Customer updated successfully"),
    @ApiResponse(code = 403, message = "Customer update forbidden", response = ErrorInfo.class),

  })
  @RequestMapping(value = "/customers/{id}", method = RequestMethod.PUT, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<CustomerSearchResult> updateCustomer(@PathVariable Integer id,
                                                             @RequestBody @ApiParam("Map containing field names with their new values.") Map<String, Object> fields) {
    CustomerJson updatedCustomer = customerUpdateService.update(id, fields);
    return ResponseEntity.ok(customerSearchResultMapper.mapToSearchResult(updatedCustomer));
  }

  @ApiOperation(value = "Get customer by ID",
    authorizations = @Authorization(value ="api_key"),
    produces = "application/json"
  )
  @ApiResponses( value = {
    @ApiResponse(code = 200, message = "Customer retrieved successfully"),
    @ApiResponse(code = 404, message = "Customer not found", response = ErrorInfo.class),
  })
  @RequestMapping(value = "/customers/{id}", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<CustomerJson> getCustomerById(@PathVariable Integer id) {
    return ResponseEntity.ok(customerService.findCustomerById(id));
  }

  @ApiOperation(value = "List contacts of a customer",
    authorizations = @Authorization(value ="api_key"),
    produces = "application/json",
    response = ContactJson.class,
    responseContainer="List"
  )
  @ApiResponses( value = {
    @ApiResponse(code = 200, message = "Contacts retrieved successfully", response = ContactJson.class, responseContainer="List")
  })
  @RequestMapping(value = "/customers/{customerId}/contacts", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<List<ContactJson>> getContacts(@PathVariable Integer customerId) {
    List<ContactJson> contacts = contactService.findByCustomer(customerId);
    return ResponseEntity.ok(contacts);
  }

  @ApiOperation(value = "Create a new contact",
    authorizations = @Authorization(value = "api_key"),
    consumes = "application/json",
    produces = "application/json",
    response = ContactJson.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Contact created successfully", response = ContactJson.class),
    @ApiResponse(code = 400, message = "Invalid contact data", response = ErrorInfo.class),
    @ApiResponse(code = 403, message = "Contact addition forbidden", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/customers/{customerId}/contacts", method = RequestMethod.POST,
    produces = "application/json", consumes = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<ContactJson> createContact(@PathVariable Integer customerId,
                                                   @RequestBody @Valid ContactJson contactJson) {
    contactJson.setCustomerId(customerId);
    ContactJson createdContact = contactService.createContact(contactJson);
    return ResponseEntity.ok(createdContact);
  }

  @ApiOperation(value = "Update a contact",
    notes =
      "<p>Data is given as key/value pair updated field being the key and it's new value (as JSON) the value. "
        + "All fields that are not marked as read only can be updated through this API.</p>",
    authorizations = @Authorization(value ="api_key"),
    produces = "application/json"
  )
  @ApiResponses( value = {
    @ApiResponse(code = 200, message = "Contact updated successfully"),
    @ApiResponse(code = 403, message = "Contact update forbidden", response = ErrorInfo.class),

  })
  @RequestMapping(value = "/customers/{customerId}/contacts/{contactId}", method = RequestMethod.PUT, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<ContactJson> updateContact(@PathVariable Integer customerId,
                                                   @PathVariable Integer contactId,
                                                   @RequestBody @ApiParam("Map containing field names with their new values.") Map<String, Object> fields) {

    ContactJson updatedContact = customerUpdateService.updateContact(customerId, contactId, fields);
    return ResponseEntity.ok(updatedContact);
  }
}
