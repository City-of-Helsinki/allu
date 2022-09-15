package fi.hel.allu.supervision.api.controller;


import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.servicecore.domain.ContactJson;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.service.ContactService;
import fi.hel.allu.servicecore.service.CustomerService;
import fi.hel.allu.supervision.api.domain.ContactSearchParameters;
import fi.hel.allu.supervision.api.domain.ContactSearchResult;
import fi.hel.allu.supervision.api.domain.CustomerSearchParameters;
import fi.hel.allu.supervision.api.domain.CustomerSearchResult;
import fi.hel.allu.supervision.api.mapper.CustomerSearchParameterMapper;
import fi.hel.allu.supervision.api.mapper.CustomerSearchResultMapper;
import fi.hel.allu.supervision.api.mapper.MapperUtil;
import fi.hel.allu.supervision.api.service.CustomerUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Customers")
public class CustomerController {

    private final CustomerService customerService;

    private final CustomerSearchResultMapper customerSearchResultMapper;

    private final CustomerUpdateService customerUpdateService;

    private final ContactService contactService;

    public CustomerController(CustomerService customerService, CustomerSearchResultMapper customerSearchResultMapper,
                              CustomerUpdateService customerUpdateService, ContactService contactService) {
        this.customerService = customerService;
        this.customerSearchResultMapper = customerSearchResultMapper;
        this.customerUpdateService = customerUpdateService;
        this.contactService = contactService;
    }

    @Operation(summary = "Search customers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customers retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CustomerSearchResult.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))})
    @PostMapping(value = "/customers/search", produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<Page<CustomerSearchResult>> search(
            @RequestBody @Valid CustomerSearchParameters customerSearchParameters) {
        QueryParameters queryParameters = CustomerSearchParameterMapper.mapToQueryParameters(customerSearchParameters);
        Pageable pageable = MapperUtil.mapToPageRequest(customerSearchParameters);
        Page<CustomerJson> result = customerService.search(queryParameters, pageable);
        Page<CustomerSearchResult> response = result.map(customerSearchResultMapper::mapToSearchResult);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Search contacts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contacts retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ContactSearchResult.class)))})
    @PostMapping(value = "/contacts/search", produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<Page<ContactSearchResult>> searchContact(
            @RequestBody @Valid ContactSearchParameters contactSearchParameters) {
        QueryParameters queryParameters = CustomerSearchParameterMapper.mapToQueryParameters(contactSearchParameters);
        Pageable pageable = MapperUtil.mapToPageRequest(contactSearchParameters);
        Page<ContactJson> result = contactService.search(queryParameters, pageable);
        Page<ContactSearchResult> response = result.map(customerSearchResultMapper::mapToSearchResult);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a new customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer created successfully",
                    content = @Content(schema = @Schema(implementation = CustomerSearchResult.class))),
            @ApiResponse(responseCode = "400", description = "Invalid customer data",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "403", description = "Customer addition forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))})
    @PostMapping(value = "/customers", produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<CustomerSearchResult> create(@RequestBody @Valid CustomerJson customerJson) {
        CustomerJson createdCustomer = customerService.createCustomer(customerJson);
        return ResponseEntity.ok(customerSearchResultMapper.mapToSearchResult(createdCustomer));
    }

    @Operation(summary = "Update a customer",
            description = "<p>Data is given as key/value pair updated field being the " + "key and it's new value (as" +
					" JSON) " +
                    "the " + "value. " + "All fields that are not marked as read only can be " + "updated through " +
					"this API.</p>")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
                    @ApiResponse(responseCode = "403", description = "Customer update forbidden",
                            content = @Content(schema = @Schema(implementation = ErrorInfo.class))),

            })
    @PutMapping(value = "/customers/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<CustomerSearchResult> updateCustomer(@PathVariable Integer id, @RequestBody @Parameter(
            description = "Map containing " + "field names with their new " + "values.") Map<String, Object> fields) {
        CustomerJson updatedCustomer = customerUpdateService.update(id, fields);
        return ResponseEntity.ok(customerSearchResultMapper.mapToSearchResult(updatedCustomer));
    }

    @Operation(summary = "Get customer by ID")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Customer retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Customer not found",
                            content = @Content(schema = @Schema(implementation = ErrorInfo.class))),})
    @GetMapping(value = "/customers/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<CustomerJson> getCustomerById(@PathVariable Integer id) {
        return ResponseEntity.ok(customerService.findCustomerById(id));
    }

    @Operation(summary = "List contacts of a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contacts retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ContactJson.class)))})
    @GetMapping(value = "/customers/{customerId}/contacts", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<List<ContactJson>> getContacts(@PathVariable Integer customerId) {
        List<ContactJson> contacts = contactService.findByCustomer(customerId);
        return ResponseEntity.ok(contacts);
    }

    @Operation(summary = "Create a new contact")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contact created successfully",
                    content = @Content(schema = @Schema(implementation = ContactJson.class))),
            @ApiResponse(responseCode = "400", description = "Invalid contact data",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "403", description = "Contact addition forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))})
    @PostMapping(value = "/customers/{customerId}/contacts", produces = "application/json",
            consumes = "application" + "/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<ContactJson> createContact(@PathVariable Integer customerId,
                                                     @RequestBody @Valid ContactJson contactJson) {
        contactJson.setCustomerId(customerId);
        ContactJson createdContact = contactService.createContact(contactJson);
        return ResponseEntity.ok(createdContact);
    }

    @Operation(summary = "Update a contact",
            description = "<p>Data is given as key/value pair updated field being the " + "key and it's new value (as" +
					" JSON) " +
                    "the " + "value. " + "All fields that are not marked as read only can be " + "updated through " +
					"this API.</p>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contact updated successfully"),
            @ApiResponse(responseCode = "403", description = "Contact update forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),

    })
    @PutMapping(value = "/customers/{customerId}/contacts/{contactId}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<ContactJson> updateContact(@PathVariable Integer customerId, @PathVariable Integer contactId,
                                                     @RequestBody @Parameter(
                                                             description =
                                                                     "Map containing field names" + " with their new " + "values" +
                                                                             ".") Map<String, Object> fields) {

        ContactJson updatedContact = customerUpdateService.updateContact(customerId, contactId, fields);
        return ResponseEntity.ok(updatedContact);
    }
}
