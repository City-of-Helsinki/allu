package fi.hel.allu.external.controller.maintenance;

import fi.hel.allu.external.domain.InvoicingCustomerExt;
import fi.hel.allu.external.mapper.CustomerExtMapper;
import fi.hel.allu.model.domain.CustomerUpdateLog;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Public interface for managing customer information.
 */
@RestController
@RequestMapping("/v1/customers")
@Tag(name = "Customer", description = "Used only for maintenance")
public class CustomerController {

    private final CustomerExtMapper customerMapper;

    private final CustomerService customerService;

    private final ApplicationServiceComposer applicationServiceComposer;

    public CustomerController(CustomerExtMapper customerMapper, CustomerService customerService,
                              ApplicationServiceComposer applicationServiceComposer) {
        this.customerMapper = customerMapper;
        this.customerService = customerService;
        this.applicationServiceComposer = applicationServiceComposer;
    }

    /**
     * Updates customer's properties which have non null value in request JSON.
     */
    @PatchMapping
    @PreAuthorize("hasAnyRole('ROLE_SERVICE')")
    public ResponseEntity<Void> merge(@RequestBody InvoicingCustomerExt customer) {
        CustomerJson customerJson = customerService.findCustomerById(customer.getId());
        boolean addsSapNumber = addsSapNumber(customerJson, customer);
        customerMapper.mergeCustomerJson(customerJson, customer);
        customerService.updateCustomerWithInvoicingInfo(customerJson.getId(), customerJson);
        if (addsSapNumber) {
            applicationServiceComposer.releaseCustomersInvoices(customer.getId());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private boolean addsSapNumber(CustomerJson customerOld, InvoicingCustomerExt customerNew) {
        return StringUtils.isBlank(customerOld.getSapCustomerNumber()) && StringUtils.isNotBlank(
                customerNew.getSapCustomerNumber());
    }

    @GetMapping(value = "/saporder/count")
    @PreAuthorize("hasAnyRole('ROLE_SERVICE')")
    public ResponseEntity<Integer> getNumberOfInvoiceRecipientsWithoutSapNumber() {
        Integer result = customerService.getNumberInvoiceRecipientsWithoutSapNumber();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/sapupdates/count")
    @PreAuthorize("hasAnyRole('ROLE_SERVICE')")
    public ResponseEntity<Integer> getNumberOfSapCustomerUpdates() {
        int result = (int) customerService.getCustomerUpdateLog().stream().map(CustomerUpdateLog::getCustomerId)
                .distinct().count();
        return new ResponseEntity<>(Integer.valueOf(result), HttpStatus.OK);
    }
}
