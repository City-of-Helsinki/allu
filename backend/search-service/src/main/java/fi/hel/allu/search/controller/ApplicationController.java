package fi.hel.allu.search.controller;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.service.GenericSearchService;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

  private GenericSearchService applicationSearchService;

  @Autowired
  public ApplicationController(
      ElasticSearchMappingConfig elasticSearchMappingConfig,
      Client client) {
    applicationSearchService = new GenericSearchService(
        elasticSearchMappingConfig,
        client,
        ElasticSearchMappingConfig.APPLICATION_INDEX_NAME,
        ElasticSearchMappingConfig.APPLICATION_TYPE_NAME);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Void> create(@RequestBody ApplicationES applicationES) {
    applicationSearchService.insert(applicationES.getId().toString(), applicationES);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/update", method = RequestMethod.PUT)
  public ResponseEntity<Void> update(@RequestBody(required = true) List<ApplicationES> applicationESs) {
    Map<String, Object> idToApplication = applicationESs.stream().collect(Collectors.toMap(a -> a.getId().toString(), a -> a));
    applicationSearchService.bulkUpdate(idToApplication);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable String id) {
    applicationSearchService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/index", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteIndex() {
    applicationSearchService.deleteIndex();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<List<Integer>> search(@Valid @RequestBody QueryParameters queryParameters) {
    return new ResponseEntity<>(applicationSearchService.findByField(queryParameters), HttpStatus.OK);
  }
}
