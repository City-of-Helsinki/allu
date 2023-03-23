package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.model.dao.CustomerUpdateLogDao;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.service.ApplicationService;
import fi.hel.allu.model.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customers")
public class CustomerController {

  @Autowired
  private CustomerService customerService;
  @Autowired
  private ApplicationService applicationService;
  @Autowired
  private CustomerUpdateLogDao customerUpdateLogDao;

  /**
   * Find a customer by database ID
   *
   * @param id customer's database ID
   * @return the customer's data
   */
  @GetMapping(value = "/{id}")
  public ResponseEntity<Customer> findCustomer(@PathVariable int id) {
    return new ResponseEntity<>(customerService.findById(id), HttpStatus.OK);
  }

  /**
   * Find a number of customers by their database IDs
   *
   * @param ids list of customer IDs to search for
   * @return list of found customers
   */
  @PostMapping(value = "/find")
  public ResponseEntity<List<Customer>> findCustomers(@RequestBody List<Integer> ids) {
    return new ResponseEntity<>(customerService.findByIds(ids), HttpStatus.OK);
  }

  /**
   * Find all customers, with paging support
   *
   * @param pageRequest page request for the search
   */
  @RequestMapping()
  public ResponseEntity<Page<Customer>> findAll(
      @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.DEFAULT_PAGE_SIZE)
      Pageable pageRequest) {
    return new ResponseEntity<>(customerService.findAll(pageRequest), HttpStatus.OK);
  }

  /**
   * Find customers by their business ids. Several customers may have the same
   * business id.
   *
   * @param businessId Business id to be searched.
   * @return list of found customers
   */
  @GetMapping(value = "/businessid/{businessId}")
  public ResponseEntity<List<Customer>> findCustomersByBusinessId(@PathVariable String businessId) {
    return new ResponseEntity<>(customerService.findByBusinessId(businessId), HttpStatus.OK);
  }

  /**
   * Returns application ids of the applications having given customer.
   *
   * @param id    id of the customer whose related applications are returned.
   * @return  List of application ids. Never <code>null</code>.
   */
  @GetMapping(value = "/applications/{id}")
  public ResponseEntity<Map<Integer, List<CustomerRoleType>>> findApplicationsByCustomer(@PathVariable int id) {
    return new ResponseEntity<>(applicationService.findByCustomer(id), HttpStatus.OK);
  }

  /**
   * Returns application ids of the applications having given customer as invoice recipient.
   *
   * @param id    id of the customer whose related applications are returned.
   * @return  List of application ids. Never <code>null</code>.
   */
  @GetMapping(value = "/invoicerecipients/{id}/applications")
  public ResponseEntity<List<Integer>> findApplicationIdsByInvoiceRecipient(@PathVariable int id) {
    return new ResponseEntity<>(applicationService.findByInvoiceRecipient(id), HttpStatus.OK);
  }

  /**
   * Update customer data
   *
   * @param id Customer's database ID
   * @param customerChange customer change request
   * @return updated customer data
   */
  @PutMapping(value = "/{id}")
  public ResponseEntity<Customer> updateCustomer(@PathVariable int id,
      @Valid @RequestBody(required = true) CustomerChange customerChange) {
    Customer resultCustomer = customerService.update(id, customerChange.getCustomer(), customerChange.getUserId());
    return new ResponseEntity<>(resultCustomer, resultCustomer != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
  }

  /**
   * Insert new customer into database
   *
   * @param customerChange customer change request
   * @return created Customer object
   */
  @PostMapping
  public ResponseEntity<Customer> addCustomer(@Valid @RequestBody(required = true) CustomerChange customerChange) {
    return new ResponseEntity<>(customerService.insert(customerChange.getCustomer(), customerChange.getUserId()),
        HttpStatus.OK);
  }

  /**
   * Get customer's change history
   *
   * @param id the customer's database ID
   * @return list of changes for the customer
   */
  @GetMapping(value = "/{id}/history")
  public ResponseEntity<List<ChangeHistoryItem>> getChanges(@PathVariable int id) {
    return new ResponseEntity<>(customerService.getCustomerChanges(id), HttpStatus.OK);
  }

  /**
   * Returns invoice recipients without SAP customer number
   * @return
   */
  @GetMapping(value = "/sap_id_missing")
  public ResponseEntity<List<InvoiceRecipientCustomer>> findInvoiceRecipientsWithoutSapNumber() {
    return new ResponseEntity<>(customerService.findInvoiceRecipientsWithoutSapNumber(), HttpStatus.OK);
  }

  /**
   * Returns number of invoice recipients without SAP customer number
   * @return
   */
  @GetMapping(value = "/sap_id_missing/count")
  public ResponseEntity<Integer> getNumberOfInvoiceRecipientsWithoutSapNumber() {
    return new ResponseEntity<>(customerService.getNumberInvoiceRecipientsWithoutSapNumber(), HttpStatus.OK);
  }

  @GetMapping(value = "/updatelog")
  public ResponseEntity<List<CustomerUpdateLog>> getSapCustomerUpdateLog() {
    return ResponseEntity.ok(customerUpdateLogDao.getUnprocessedUpdates());
  }

  @PutMapping(value = "/updatelog/processed")
  public ResponseEntity<Void> setUpdateLogProcessed(@RequestBody List<Integer> logIds) {
    customerUpdateLogDao.setUpdateLogsProcessed(logIds);
    return new ResponseEntity<>(HttpStatus.OK);
  }


}