package fi.hel.allu.external.controller;

import fi.hel.allu.external.domain.CustomerExt;
import fi.hel.allu.external.domain.CustomerWithContactsExt;
import fi.hel.allu.external.mapper.CustomerExtMapper;
import fi.hel.allu.servicecore.service.ContactService;
import fi.hel.allu.servicecore.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL')")
  public ResponseEntity<CustomerExt> create(@Valid @RequestBody CustomerExt customer) {
    return new ResponseEntity<>(
        CustomerExtMapper.mapCustomerExt(customerService.createCustomer(CustomerExtMapper.mapCustomerJson(customer))),
        HttpStatus.OK);
  }
}
