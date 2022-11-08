package fi.hel.allu.supervision.api.controller;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.mapper.CustomerMapper;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.ApprovalDocumentService;
import fi.hel.allu.servicecore.service.ChargeBasisService;
import fi.hel.allu.servicecore.service.LocationService;
import fi.hel.allu.supervision.api.domain.BaseApplication;
import fi.hel.allu.supervision.api.mapper.ApplicationMapperCollector;
import fi.hel.allu.supervision.api.service.ApplicationUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SecurityRequirement(name = "bearerAuth")
public abstract class BaseApplicationDetailsController<A extends BaseApplication<?>, U extends CreateApplicationJson> {


    private final ApprovalDocumentService approvalDocumentService;
    private final ChargeBasisService chargeBasisService;
    private final ApplicationUpdateService applicationUpdateService;
    protected ApplicationServiceComposer applicationServiceComposer;
    protected LocationService locationService;
    protected ApplicationMapper applicationMapper;
    protected CustomerMapper customerMapper;
    protected BaseApplicationDetailsController(ApprovalDocumentService approvalDocumentService,
                                               ChargeBasisService chargeBasisService,
                                               ApplicationServiceComposer applicationServiceComposer,
                                               ApplicationUpdateService applicationUpdateService,
                                               LocationService locationService,
                                               ApplicationMapperCollector applicationMapperCollector) {
        this.approvalDocumentService = approvalDocumentService;
        this.chargeBasisService = chargeBasisService;
        this.applicationServiceComposer = applicationServiceComposer;
        this.applicationUpdateService = applicationUpdateService;
        this.locationService = locationService;
        this.applicationMapper = applicationMapperCollector.getApplicationMapper();
        this.customerMapper = applicationMapperCollector.getCustomerMapper();
    }

    protected abstract ApplicationType getApplicationType();

    protected abstract A mapApplication(ApplicationJson application);

    @Operation(summary = "Get application details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application retrieved successfully"),
    })
    @GetMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<A> getApplicationDetails(@PathVariable Integer id) {
        ApplicationJson application = applicationServiceComposer.findApplicationById(id);
        validateType(application);
        return ResponseEntity.ok(mapApplication(application));
    }

    @Operation(summary = "Get applications with given list of IDs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Applications retrieved successfully"),
    })
    @GetMapping(produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<List<A>> getApplicationsWithIds(@RequestParam("ids") final List<Integer> ids) {
        List<ApplicationJson> applications = applicationServiceComposer.findApplicationsByIds(ids);
        applications.forEach(this::validateType);
        return ResponseEntity.ok(applications.stream().map(this::mapApplication).collect(Collectors.toList()));
    }

    @Operation(summary = "Update application with given version number.",
            description =
                    "<p>Data is given as key/value pair updated field being the key and it's new value (as JSON) the " +
                            "value. "
                            + "All fields that are not marked as read only can be updated through this API.</p>"
                            + "<p>Update is allowed only if the status of the application is PENDING and application " +
                            "is not an external application "
                            + "or status of the application is HANDLING, PRE_RESERVED or RETURNED_TO_PREPARATION.</p>"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application updated successfully"),
            @ApiResponse(responseCode = "409",
                    description = "Update failed, given version of application updated by another user",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "403", description = "Application update forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),

    })
    @PutMapping(value = "/{id}/version/{version}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<A> updateApplication(@PathVariable Integer id, @PathVariable Integer version,
                                               @RequestBody @Parameter(
                                                       description = "Map containing field names with their new " +
                                                               "values.")
                                               Map<String, Object> fields) {
        validateType(id);
        ApplicationJson updatedApplication = applicationUpdateService.update(id, version, fields);
        return ResponseEntity.ok(mapApplication(updatedApplication));
    }

    @Operation(summary = "Create new application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid application data",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "403", description = "Application creation forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<A> createApplication(
            @RequestBody @Valid @Parameter(description = "New application") U application) {
        application.setType(getApplicationType());

        ApplicationJson newApplication = applicationMapper.mapCreateJsonToApplicationJson(application);
        ApplicationJson createdApplication = applicationServiceComposer.createApplication(newApplication);
        return ResponseEntity.ok(mapApplication(createdApplication));
    }

    protected void validateType(Integer applicationId) {
        ApplicationJson application = applicationServiceComposer.findApplicationById(applicationId);
        validateType(application);
    }

    protected void validateType(ApplicationJson application) {
        if (application.getType() != getApplicationType()) {
            throw new IllegalOperationException("applicationtype.invalid");
        }
    }

    protected ResponseEntity<byte[]> getApprovalDocument(Integer applicationId, ApprovalDocumentType type) {
        return pdfResult(approvalDocumentService.getApprovalDocument(applicationId, type,
                                                                     chargeBasisService.getUnlockedAndInvoicableChargeBasis(
                                                                             applicationId)));
    }

    protected ResponseEntity<byte[]> pdfResult(byte[] bytes) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_PDF);
        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

    @Operation(summary = "Update applicant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid customer data",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "403", description = "Customer update forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<CustomerWithContactsJson> updateCustomerApplicant(Integer applicationId,
                                                                            CreateCustomerWithContactsJson customer) {
        validateType(applicationId);
        CustomerWithContactsJson result = applicationServiceComposer.replaceCustomerWithContacts(applicationId,
                                                                                                 customerMapper.createCustomerWithContactsJson(
                                                                                                         CustomerRoleType.APPLICANT,
                                                                                                         customer));
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update property developer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "403", description = "Customer update forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
    })
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<CustomerWithContactsJson> updateCustomerPropertyDeveloper(Integer applicationId,
                                                                                    CreateCustomerWithContactsJson customer) {
        validateType(applicationId);
        CustomerWithContactsJson result = applicationServiceComposer.replaceCustomerWithContacts(applicationId,
                                                                                                 customerMapper.createCustomerWithContactsJson(
                                                                                                         CustomerRoleType.PROPERTY_DEVELOPER,
                                                                                                         customer));
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update contractor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "403", description = "Customer update forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
    })
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<CustomerWithContactsJson> updateCustomerContractor(Integer applicationId,
                                                                             CreateCustomerWithContactsJson customer) {
        validateType(applicationId);
        CustomerWithContactsJson result = applicationServiceComposer.replaceCustomerWithContacts(applicationId,
                                                                                                 customerMapper.createCustomerWithContactsJson(
                                                                                                         CustomerRoleType.CONTRACTOR,
                                                                                                         customer));
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update representative")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "403", description = "Customer update forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
    })
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<CustomerWithContactsJson> updateCustomerRepresentative(Integer applicationId,
                                                                                 CreateCustomerWithContactsJson customer) {
        validateType(applicationId);
        CustomerWithContactsJson result = applicationServiceComposer.replaceCustomerWithContacts(applicationId,
                                                                                                 customerMapper.createCustomerWithContactsJson(
                                                                                                         CustomerRoleType.REPRESENTATIVE,
                                                                                                         customer));
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update distribution list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Distribution list updated successfully"),
            @ApiResponse(responseCode = "403", description = "Distribution list update forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
    })
    @PutMapping(value = "/{id}/distributionList", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<ApplicationJson> updateDistributionList(@PathVariable Integer id,
                                                                  @RequestBody
                                                                  @Parameter(description = "The new distribution list")
                                                                  List<DistributionEntryJson> distributionList) {
        validateType(id);
        ApplicationJson result = applicationServiceComposer.replaceDistributionList(id, distributionList);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update invoice recipient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice recipient updated successfully"),
            @ApiResponse(responseCode = "403", description = "Invoice recipient update forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
    })
    @PutMapping(value = "/{id}/invoiceRecipient", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<Void> updateInvoiceRecipient(@PathVariable Integer id,
                                                       @RequestBody
                                                       @Parameter(description = "The new invoice recipient id")
                                                       Integer invoiceRecipientId) {
        validateType(id);
        applicationServiceComposer.setInvoiceRecipient(id, invoiceRecipientId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove property developer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Property developer removed successfully"),
            @ApiResponse(responseCode = "403", description = "Removal forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
    })
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<Void> removePropertyDeveloper(Integer applicationId) {
        return removeCustomerWithRole(applicationId, CustomerRoleType.PROPERTY_DEVELOPER);
    }

    @Operation(summary = "Remove representative")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Representative removed successfully"),
            @ApiResponse(responseCode = "403", description = "Removal forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
    })
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<Void> removeRepresentative(Integer applicationId) {
        return removeCustomerWithRole(applicationId, CustomerRoleType.REPRESENTATIVE);
    }

    private ResponseEntity<Void> removeCustomerWithRole(Integer applicationId, CustomerRoleType roleType) {
        validateType(applicationId);
        applicationServiceComposer.removeCustomerWithContacts(applicationId, roleType);
        return ResponseEntity.ok().build();
    }

    protected void validateUpdateAllowed(Integer applicationId) {
        applicationUpdateService.validateUpdateAllowed(applicationId);
    }
}
