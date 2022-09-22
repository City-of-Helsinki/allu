package fi.hel.allu.search.controller;

import fi.hel.allu.search.domain.ProjectES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.service.ProjectSearchService;

import fi.hel.allu.search.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

  private final ProjectSearchService projectSearchService;

  @Autowired
  public ProjectController(ProjectSearchService projectSearchService) {
    this.projectSearchService = projectSearchService;
  }


  @PostMapping
  public ResponseEntity<Void> create(@RequestBody ProjectES projectES) {
    projectSearchService.insert(projectES);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping(value = "/{id}")
  public ResponseEntity<Void> update(
      @PathVariable String id,
      @RequestBody ProjectES projectES) {
    projectES.setId(Integer.parseInt(id));
    projectSearchService.bulkUpdate(Collections.singletonList(projectES));
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping(value = "/update")
  public ResponseEntity<Void> update(@RequestBody List<ProjectES> projectESs) {
    projectSearchService.bulkUpdate(projectESs);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(
      @PathVariable String id) {
    projectSearchService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }


  @DeleteMapping(value = "/index")
  public ResponseEntity<Void> deleteIndex() {
    projectSearchService.deleteIndex();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping(value = "/search")
  public ResponseEntity<Page<Integer>> search(@Valid @RequestBody QueryParameters queryParameters,
      @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.DEFAULT_PAGE_SIZE) Pageable pageRequest) {
    return new ResponseEntity<>(projectSearchService.findByField(queryParameters, pageRequest, false), HttpStatus.OK);
  }

  @PostMapping(value = "/sync/data")
  public ResponseEntity<Void> syncData(@Valid @RequestBody List<ProjectES> projectESs) {
    projectSearchService.syncData(projectESs);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
