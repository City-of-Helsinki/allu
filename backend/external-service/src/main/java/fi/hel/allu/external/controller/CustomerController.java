package fi.hel.allu.external.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.external.domain.CustomerExt;
import fi.hel.allu.external.domain.CustomerWithContactsExt;
import fi.hel.allu.external.mapper.CustomerExtMapper;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.service.ContactService;
import fi.hel.allu.servicecore.service.CustomerService;

/**
 * Public interface for managing customer information.
 */
@RestController
@RequestMapping("/v1/customers")
public class CustomerController {

  @Autowired
  CustomerService customerService;
  @Autowired
  ContactService contactService;

  /**
   * Returns list of customers with their related contacts connected to the current interface user.
   *
   * @return
   */
  @RequestMapping(value = "/connected", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<List<CustomerWithContactsExt>> getConnectedCustomers() {
    // TODO: find customers connected to the token and return their information
    throw new UnsupportedOperationException("not implemented");
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<CustomerExt> findById(@PathVariable int id) {
    return new ResponseEntity<>(CustomerExtMapper.mapCustomerExt(customerService.findCustomerById(id)), HttpStatus.OK);
  }

  @RequestMapping(value = "/businessid/{businessId}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<List<CustomerExt>> findByBusinessId(@PathVariable String businessId) {
    return new ResponseEntity<>(customerService.findCustomerByBusinessId(businessId)
        .stream().map(c -> CustomerExtMapper.mapCustomerExt(c)).collect(Collectors.toList()),
        HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL')")
  public ResponseEntity<CustomerExt> create(@Valid @RequestBody CustomerExt customer) {
    return new ResponseEntity<>(
        CustomerExtMapper.mapCustomerExt(customerService.createCustomer(CustomerExtMapper.mapCustomerJson(customer))),
        HttpStatus.OK);
  }

  /**
   * Updates customer's properties which have non null value in request JSON.
   */
  @RequestMapping(method = RequestMethod.PATCH)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL')")
  public ResponseEntity<Void> merge(@RequestBody CustomerExt customer) {
    CustomerJson customerJson = customerService.findCustomerById(customer.getId());
    CustomerExtMapper.mergeCustomerJson(customerJson, customer);
    customerService.updateCustomer(customerJson.getId(), customerJson);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<CustomerExt> update(@Valid @RequestBody CustomerExt customer) {
    // TODO: ROLE_TRUSTED_PARTNER should be allowed to edit only the customers connected to the token i.e. what getConnectedCustomers() returns
    return new ResponseEntity<>(
        CustomerExtMapper.mapCustomerExt(customerService.updateCustomer(customer.getId(), CustomerExtMapper.mapCustomerJson(customer))),
        HttpStatus.OK);
  }
}
