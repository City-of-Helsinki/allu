package fi.hel.allu.ui.controller;

import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.service.ContactService;
import fi.hel.allu.servicecore.service.CustomerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for managing customer information.
 */
@RestController
@RequestMapping("/customers")
public class CustomerController {

  private static final String CUSTOMER_EXPORT_FILENAME = "allu_customers_";

  @Autowired
  CustomerService customerService;
  @Autowired
  ContactService contactService;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<CustomerJson> findById(@PathVariable int id) {
    return new ResponseEntity<>(customerService.findCustomerById(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/findByIds", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<CustomerJson>> findByIds(@RequestBody List<Integer> ids) {
    return new ResponseEntity<>(customerService.getCustomersById(ids), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/contacts", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ContactJson>> findByCustomer(@PathVariable int id) {
    return new ResponseEntity<>(contactService.findByCustomer(id), HttpStatus.OK);
  }

  /**
   * Get change items for a customer
   *
   * @param id Customer's database id
   * @return list of changes ordered from oldest to newest
   */
  @RequestMapping(value = "/{id}/history", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ChangeHistoryItemJson>> getChanges(@PathVariable int id) {
    return new ResponseEntity<>(customerService.getChanges(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<CustomerJson>> search(@Valid @RequestBody QueryParametersJson queryParameters) {
    return new ResponseEntity<>(customerService.search(queryParameters), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_DECISION')")
  public ResponseEntity<CustomerJson> create(@Valid @RequestBody(required = true) CustomerJson customer) {
    return new ResponseEntity<>(customerService.createCustomer(customer), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_DECISION')")
  public ResponseEntity<CustomerJson> update(@PathVariable int id, @Valid @RequestBody(required = true) CustomerJson customer) {
    return new ResponseEntity<>(customerService.updateCustomer(id, customer), HttpStatus.OK);
  }

  @RequestMapping(value = "/withcontacts", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_DECISION')")
  public ResponseEntity<CustomerWithContactsJson> createWithContacts(@Valid @RequestBody CustomerWithContactsJson customerWithContactsJson) {
    return new ResponseEntity<>(customerService.createCustomerWithContacts(customerWithContactsJson), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/withcontacts", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_DECISION')")
  public ResponseEntity<CustomerWithContactsJson> updateWithContacts(
      @PathVariable int id,
      @Valid @RequestBody CustomerWithContactsJson customerWithContactsJson) {
    return new ResponseEntity<>(customerService.updateCustomerWithContacts(id, customerWithContactsJson), HttpStatus.OK);
  }

  /**
   * Creates CSV containing invoice recipients without SAP customer number.
   */
  @RequestMapping(value = "/saporder/csv", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INVOICING')")
  public ResponseEntity<Void> getSapCustomerOrderCSV(HttpServletResponse response) throws IOException {
    response.setContentType("text/csv; charset=utf-8");
    response.setHeader("Content-Disposition", "attachment; filename=" + getCustomerExportFileName() + ".csv");
    new CustomerCsvWriter(response.getWriter(), customerService.findInvoiceRecipientsWithoutSapNumber()).write();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Creates CSV containing invoice recipients without SAP customer number.
   */
  @RequestMapping(value = "/saporder/xlsx", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INVOICING')")
  public ResponseEntity<Void> getSapCustomerOrderExcel(HttpServletResponse response) throws IOException {
    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader("Content-Disposition", "attachment; filename=" + getCustomerExportFileName() + ".xlsx");
    new CustomerExcelWriter(response.getOutputStream(), customerService.findInvoiceRecipientsWithoutSapNumber()).write();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  private String getCustomerExportFileName() {
    return CUSTOMER_EXPORT_FILENAME + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
  }
}
