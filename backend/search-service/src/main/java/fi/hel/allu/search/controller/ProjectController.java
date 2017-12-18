package fi.hel.allu.search.controller;

import fi.hel.allu.search.domain.ProjectES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.service.ProjectSearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

  private ProjectSearchService projectSearchService;

  @Autowired
  public ProjectController(ProjectSearchService projectSearchService) {
    this.projectSearchService = projectSearchService;
  }


  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Void> create(@RequestBody ProjectES projectES) {
    projectSearchService.insert(projectES);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Void> update(
      @PathVariable String id,
      @RequestBody(required = true) ProjectES projectES) {
    projectES.setId(Integer.parseInt(id));
    projectSearchService.bulkUpdate(Collections.singletonList(projectES));
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/update", method = RequestMethod.PUT)
  public ResponseEntity<Void> update(@RequestBody(required = true) List<ProjectES> projectESs) {
    projectSearchService.bulkUpdate(projectESs);
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
