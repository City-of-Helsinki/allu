package fi.hel.allu.search.controller;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.CustomerES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.service.GenericSearchService;
import fi.hel.allu.search.util.CustomersIndexUtil;
import org.elasticsearch.client.Client;
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

  private GenericSearchService customerSearchService;
  private GenericSearchService applicationSearchService;

  @Autowired
  public CustomerController(
      ElasticSearchMappingConfig elasticSearchMappingConfig,
      Client client) {
    customerSearchService = new GenericSearchService(
        elasticSearchMappingConfig,
        client,
        ElasticSearchMappingConfig.CUSTOMER_INDEX_NAME,
        ElasticSearchMappingConfig.CUSTOMER_TYPE_NAME);
    applicationSearchService = new GenericSearchService(
        elasticSearchMappingConfig,
        client,
        ElasticSearchMappingConfig.APPLICATION_INDEX_NAME,
        ElasticSearchMappingConfig.APPLICATION_TYPE_NAME);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Void> create(@RequestBody CustomerES customerES) {
    customerSearchService.insert(customerES.getId().toString(), customerES);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/update", method = RequestMethod.PUT)
  public ResponseEntity<Void> update(@RequestBody List<CustomerES> customerESses) {
    Map<String, Object> idToCustomer = customerESses.stream().collect(Collectors.toMap(a -> a.getId().toString(), a -> a));
    customerSearchService.bulkUpdate(idToCustomer);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/applications", method = RequestMethod.PUT)
  public ResponseEntity<Void> updateCustomerOfApplications(
      @PathVariable String id,
      @RequestBody Map<Integer, List<CustomerRoleType>> applicationIdToCustomerRoleTypes) {
    CustomerES customerES = customerSearchService.findObjectById(id, CustomerES.class)
        .orElseThrow(() -> new NoSuchEntityException("No such customer in ElasticSearch", id));
    Map<String, Object> idToCustomer = applicationIdToCustomerRoleTypes.entrySet().stream().collect(Collectors.toMap(
        acrt -> Integer.toString(acrt.getKey()),
        acrt -> CustomersIndexUtil.getCustomerUpdateStructure(acrt.getValue(), customerES)));
    applicationSearchService.bulkUpdate(idToCustomer);
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
}
