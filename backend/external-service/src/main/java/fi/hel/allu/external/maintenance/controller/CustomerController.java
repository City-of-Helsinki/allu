package fi.hel.allu.external.maintenance.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.external.domain.InvoicingCustomerExt;
import fi.hel.allu.external.mapper.CustomerExtMapper;
import fi.hel.allu.model.domain.CustomerUpdateLog;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.CustomerService;

/**
 * Public interface for managing customer information.
 */
@RestController
@RequestMapping("/v1/customers")
public class CustomerController {

  @Autowired
  private CustomerExtMapper customerMapper;

  @Autowired
  CustomerService customerService;

  @Autowired
  ApplicationServiceComposer applicationServiceComposer;

  /**
   * Updates customer's properties which have non null value in request JSON.
   *
   */
  @RequestMapping(method = RequestMethod.PATCH)
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
    return StringUtils.isBlank(customerOld.getSapCustomerNumber()) && StringUtils.isNotBlank(customerNew.getSapCustomerNumber());
  }

  @RequestMapping(value="/saporder/count", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_SERVICE')")
  public ResponseEntity<Integer> getNumberOfInvoiceRecipientsWithoutSapNumber() {
    Integer result = customerService.getNumberInvoiceRecipientsWithoutSapNumber();
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @RequestMapping(value="/sapupdates/count", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_SERVICE')")
  public ResponseEntity<Integer> getNumberOfSapCustomerUpdates() {
    int result = (int)customerService.getCustomerUpdateLog().stream()
        .map(CustomerUpdateLog::getCustomerId)
        .distinct()
        .count();
    return new ResponseEntity<>(Integer.valueOf(result), HttpStatus.OK);
  }
}
