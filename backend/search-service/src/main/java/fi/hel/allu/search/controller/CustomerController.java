package fi.hel.allu.search.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import fi.hel.allu.search.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.search.domain.CustomerES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.service.ApplicationSearchService;
import fi.hel.allu.search.service.CustomerSearchService;
import fi.hel.allu.search.util.CustomersIndexUtil;

/**
 * Controller for searching and indexing customers.
 */
@RestController
@RequestMapping("/customers")
public class CustomerController {

  private final CustomerSearchService customerSearchService;
  private final ApplicationSearchService applicationSearchService;

  @Autowired
  public CustomerController(CustomerSearchService customerSearchService,
      ApplicationSearchService applicationSearchService) {
    this.customerSearchService = customerSearchService;
    this.applicationSearchService = applicationSearchService;
  }

  @PostMapping
  public ResponseEntity<Void> create(@RequestBody CustomerES customerES) {
    customerSearchService.insert(customerES);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping(value = "/update")
  public ResponseEntity<Void> update(@RequestBody List<CustomerES> customerESses) {
    customerSearchService.bulkUpdate(customerESses);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/applications")
  public ResponseEntity<Void> updateCustomerOfApplications(
      @PathVariable String id,
      @RequestBody Map<Integer, List<CustomerRoleType>> applicationIdToCustomerRoleTypes) {
    CustomerES customerES = customerSearchService.findObjectById(id)
        .orElseThrow(() -> new NoSuchEntityException("No such customer in ElasticSearch", id));
    Map<Integer, Object> idToCustomer = applicationIdToCustomerRoleTypes.entrySet().stream().collect(Collectors.toMap(
        Map.Entry::getKey,
        acrt -> CustomersIndexUtil.getCustomerUpdateStructure(acrt.getValue(), customerES)));
    applicationSearchService.partialUpdate(idToCustomer, false);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    customerSearchService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping(value = "/index")
  public ResponseEntity<Void> deleteIndex() {
    customerSearchService.deleteIndex();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping(value = "/search")
  public ResponseEntity<Page<Integer>> search(@Valid @RequestBody QueryParameters queryParameters,
      @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.DEFAULT_PAGE_SIZE) Pageable pageRequest) {
    return new ResponseEntity<>(customerSearchService.findByField(queryParameters, pageRequest, false), HttpStatus.OK);
  }

  @PostMapping(value = "/search/{type}")
  public ResponseEntity<Page<Integer>> searchByType(@PathVariable CustomerType type,
      @Valid @RequestBody QueryParameters queryParameters,
      @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.DEFAULT_PAGE_SIZE) Pageable pageRequest,
      @RequestParam(defaultValue = "false") Boolean matchAny) {
    return new ResponseEntity<>(customerSearchService.findByTypeAndField(type, queryParameters, pageRequest, matchAny), HttpStatus.OK);
  }


  @PostMapping(value = "/sync/data")
  public ResponseEntity<Void> syncData(@Valid @RequestBody List<CustomerES> customerESs) {
    customerSearchService.syncData(customerESs);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
