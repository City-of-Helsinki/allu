package fi.hel.allu.search.controller;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.search.domain.CustomerES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.service.ApplicationSearchService;
import fi.hel.allu.search.service.CustomerSearchService;
import fi.hel.allu.search.util.CustomersIndexUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for searching and indexing customers.
 */
@RestController
@RequestMapping("/customers")
public class CustomerController {

  private CustomerSearchService customerSearchService;
  private ApplicationSearchService applicationSearchService;

  @Autowired
  public CustomerController(CustomerSearchService customerSearchService,
      ApplicationSearchService applicationSearchService) {
    this.customerSearchService = customerSearchService;
    this.applicationSearchService = applicationSearchService;
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Void> create(@RequestBody CustomerES customerES) {
    customerSearchService.insert(customerES);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/update", method = RequestMethod.PUT)
  public ResponseEntity<Void> update(@RequestBody List<CustomerES> customerESses) {
    customerSearchService.bulkUpdate(customerESses);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/applications", method = RequestMethod.PUT)
  public ResponseEntity<Void> updateCustomerOfApplications(
      @PathVariable String id,
      @RequestBody Map<Integer, List<CustomerRoleType>> applicationIdToCustomerRoleTypes) {
    CustomerES customerES = customerSearchService.findObjectById(id)
        .orElseThrow(() -> new NoSuchEntityException("No such customer in ElasticSearch", id));
    Map<Integer, Object> idToCustomer = applicationIdToCustomerRoleTypes.entrySet().stream().collect(Collectors.toMap(
        acrt -> acrt.getKey(),
        acrt -> CustomersIndexUtil.getCustomerUpdateStructure(acrt.getValue(), customerES)));
    applicationSearchService.partialUpdate(idToCustomer);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable String id) {
    customerSearchService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/index", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteIndex() {
    customerSearchService.deleteIndex();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<List<Integer>> search(@Valid @RequestBody QueryParameters queryParameters) {
    return new ResponseEntity<>(customerSearchService.findByField(queryParameters), HttpStatus.OK);
  }

  @RequestMapping(value = "/sync/data", method = RequestMethod.POST)
  public ResponseEntity<Void> syncData(@Valid @RequestBody List<CustomerES> customerESs) {
    customerSearchService.syncData(customerESs);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
