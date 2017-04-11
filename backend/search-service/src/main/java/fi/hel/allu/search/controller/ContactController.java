package fi.hel.allu.search.controller;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.ContactES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.service.GenericSearchService;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for searching and indexing contacts.
 */
@RestController
@RequestMapping("/contacts")
public class ContactController {
  private GenericSearchService contactSearchService;
  private GenericSearchService applicationSearchService;

  @Autowired
  public ContactController(
      ElasticSearchMappingConfig elasticSearchMappingConfig,
      Client client) {
    contactSearchService = new GenericSearchService(
        elasticSearchMappingConfig,
        client,
        ElasticSearchMappingConfig.CUSTOMER_INDEX_NAME,
        ElasticSearchMappingConfig.CONTACT_TYPE_NAME);
    applicationSearchService = new GenericSearchService(
        elasticSearchMappingConfig,
        client,
        ElasticSearchMappingConfig.APPLICATION_INDEX_NAME,
        ElasticSearchMappingConfig.APPLICATION_TYPE_NAME);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Void> create(@RequestBody List<ContactES> contactES) {
    Map<String, Object> idToContact = contactES.stream().collect(Collectors.toMap(c -> Integer.toString(c.getId()), c -> c));
    contactSearchService.bulkInsert(idToContact);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/update", method = RequestMethod.PUT)
  public ResponseEntity<Void> update(@RequestBody List<ContactES> contactESs) {
    Map<String, Object> idToContact = contactESs.stream().collect(Collectors.toMap(a -> a.getId().toString(), a -> a));
    contactSearchService.bulkUpdate(idToContact);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/applications", method = RequestMethod.PUT)
  public ResponseEntity<Void> updateContactsOfApplications(@RequestBody Map<Integer, List<ContactES>> idToContactList) {
    Map<String, Object> idToContactUpdate = idToContactList.entrySet().stream()
        .collect(Collectors.toMap(
            entry -> Integer.toString(entry.getKey()),
            entry -> Collections.singletonMap("contacts", entry.getValue())));
    applicationSearchService.bulkUpdate(idToContactUpdate);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable String id) {
    contactSearchService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/index", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteIndex() {
    contactSearchService.deleteIndex();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<List<Integer>> search(@Valid @RequestBody QueryParameters queryParameters) {
    return new ResponseEntity<>(contactSearchService.findByField(queryParameters), HttpStatus.OK);
  }
}
