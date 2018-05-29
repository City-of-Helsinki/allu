package fi.hel.allu.model.controller;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.Project;
import fi.hel.allu.model.domain.ProjectChange;
import fi.hel.allu.model.service.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/projects")
public class ProjectController {

  @Autowired
  private ProjectService projectService;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Project> find(@PathVariable int id) {
    return new ResponseEntity<>(projectService.find(id), HttpStatus.OK);
  }

  /**
   * Find applications by application IDs
   *
   * @param   ids to be searched.
   * @return  found applications
   */
  @RequestMapping(value = "/find", method = RequestMethod.POST)
  public ResponseEntity<List<Project>> findByIds(@RequestBody List<Integer> ids) {
    List<Project> projects = projectService.findByIds(ids);
    return new ResponseEntity<>(projects, HttpStatus.OK);
  }

  /**
   * Find all projects, with paging support
   *
   * @param pageRequest page request for the search
   */
  @RequestMapping()
  public ResponseEntity<Page<Project>> findAll(
      @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.DEFAULT_PAGE_SIZE)
      Pageable pageRequest) {
    return new ResponseEntity<>(projectService.findAll(pageRequest), HttpStatus.OK);
  }

  /**
   * Returns the children of the given project.
   *
   * @param id Project whose children should be fetched.
   * @return List of children. Never <code>null</code>.
   */
  @RequestMapping(value = "/{id}/children", method = RequestMethod.GET)
  public ResponseEntity<List<Project>> findChildren(@PathVariable int id) {
    return new ResponseEntity<>(projectService.findProjectChildren(id), HttpStatus.OK);
  }

  /**
   * Returns the parents of the given project.
   *
   * @param id Project whose parents should be fetched.
   * @return  List of parents. Never <code>null</code>. The requested project itself is the first item and most grand parent project
   *          is the last item.
   */
  @RequestMapping(value = "/{id}/parents", method = RequestMethod.GET)
  public ResponseEntity<List<Project>> findParents(@PathVariable int id) {
    return new ResponseEntity<>(projectService.findProjectParents(id), HttpStatus.OK);
  }


  /**
   * Insert given project to database.
   *
   * @param project Project to be inserted.
   * @return Inserted project.
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Project> insert(@Valid @RequestBody(required = true) ProjectChange projectChange) {
    return new ResponseEntity<>(projectService.insert(
        projectChange.getProject(), projectChange.getUserId()), HttpStatus.OK);
  }

  /**
   * Update given project to database.
   *
   * @param project Project to be updated.
   * @return Updated project.
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Project> update(@PathVariable int id, @Valid @RequestBody(required = true) ProjectChange projectChange) {
    return new ResponseEntity<>(projectService.update(
        id, projectChange.getProject(), projectChange.getUserId()), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable int id) {
    projectService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/history", method = RequestMethod.GET)
  public ResponseEntity<List<ChangeHistoryItem>> getChanges(@PathVariable int id) {
    return new ResponseEntity<>(projectService.getProjectChanges(id), HttpStatus.OK);
  }

  /**
   * Find all applications for a project
   *
   * @param id
   * @return list of applications
   */
  @RequestMapping(value = "/{id}/applications", method = RequestMethod.GET)
  public ResponseEntity<List<Application>> findApplicationsByProject(@PathVariable int id) {
    return new ResponseEntity<>(projectService.findApplicationsByProject(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/applications", method = RequestMethod.PUT)
  public ResponseEntity<List<Integer>> addApplications(
      @PathVariable int id, @RequestParam(required = true) int userId,
      @Valid @RequestBody(required = true) List<Integer> applicationIds) {
    return new ResponseEntity<>(projectService.addApplications(id, applicationIds, userId), HttpStatus.OK);
  }

  @RequestMapping(value = "/applications/{appId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> removeApplication(@PathVariable int appId, @RequestParam(required = true) int userId) {
    projectService.removeApplication(appId, userId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Update parent of the given project.
   *
   * @param id            Project whose parent should be updated.
   * @param parentProject New parent project.
   * @return Updated project with new parent.
   */
  @RequestMapping(value = {"/{id}/parentProject/{parentProject}", "/{id}/parentProject"}, method = RequestMethod.PUT)
  public ResponseEntity<Project> updateProjectParent(
      @PathVariable int id, @PathVariable Optional<Integer> parentProject, @RequestParam(required = true) int userId) {
    return new ResponseEntity<>(projectService.updateProjectParent(id, parentProject.orElse(null), userId), HttpStatus.OK);
  }

  /**
   * Updates the information of given projects by going through the project / application hierarchy to find the up-to-date values. Updates
   * also all projects related to the given projects.
   *
   * @param projectIds List of projects to be updated.
   * @return Projects that have been updated. May contain more projects than in the list provided as a parameter.
   */
  @RequestMapping(value = "/update", method = RequestMethod.PUT)
  public ResponseEntity<List<Project>> updateProjectInformation(@RequestParam(required = true) int userId, @Valid @RequestBody(required = true) List<Integer> projectIds) {
    return new ResponseEntity<>(projectService.updateProjectInformation(projectIds, userId), HttpStatus.OK);
  }

  @RequestMapping(value = "/nextProjectNumber", method = RequestMethod.POST)
  public ResponseEntity<Integer> getNextProjectNumber() {
    return new ResponseEntity<>(projectService.getNextProjectNumber(), HttpStatus.OK);
  }
}
