package fi.hel.allu.search.controller;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.ProjectES;
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

@RestController
@RequestMapping("/projects")
public class ProjectController {

  private GenericSearchService projectSearchService;

  @Autowired
  public ProjectController(
      ElasticSearchMappingConfig elasticSearchMappingConfig,
      Client client
  ) {
    projectSearchService = new GenericSearchService(
        elasticSearchMappingConfig,
        client,
        ElasticSearchMappingConfig.APPLICATION_INDEX_NAME,
        ElasticSearchMappingConfig.PROJECT_TYPE_NAME);
  }


  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Void> create(@RequestBody ProjectES projectES) {
    projectSearchService.insert(projectES.getId().toString(), projectES);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Void> update(
      @PathVariable String id,
      @RequestBody(required = true) ProjectES projectES) {
    projectES.setId(Integer.parseInt(id));
    projectSearchService.update(Collections.singletonMap(projectES.getId().toString(), projectES));
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/update", method = RequestMethod.PUT)
  public ResponseEntity<Void> update(@RequestBody(required = true) List<ProjectES> projectESs) {
    Map<String, Object> idToProject = projectESs.stream().collect(Collectors.toMap(p -> p.getId().toString(), p -> p));
    projectSearchService.update(idToProject);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/index", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteIndex() {
    projectSearchService.deleteIndex();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<List<Integer>> search(@Valid @RequestBody QueryParameters queryParameters) {
    return new ResponseEntity<>(projectSearchService.findByField(queryParameters), HttpStatus.OK);
  }
}
