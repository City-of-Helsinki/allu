package fi.hel.allu.ui.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ChangeHistoryItemJson;
import fi.hel.allu.servicecore.domain.ProjectJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.ProjectService;
import fi.hel.allu.servicecore.service.ProjectServiceComposer;

/**
 * Controller for managing projects.
 */
@RestController
@RequestMapping("/projects")
public class ProjectController {

  @Autowired
  private ProjectService projectService;
  @Autowired
  private ProjectServiceComposer projectServiceComposer;
  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<Page<ProjectJson>> search(@Valid @RequestBody QueryParameters queryParameters,
      @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.DEFAULT_PAGE_SIZE, sort = "creationTime", direction = Direction.DESC)
      Pageable pageRequest) {
    return new ResponseEntity<>(projectServiceComposer.search(queryParameters, pageRequest), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<ProjectJson> findById(@PathVariable int id) {
    List<ProjectJson> projects = projectService.findByIds(Collections.singletonList(id));
    if (projects.size() != 1) {
      throw new NoSuchEntityException("project.notFound", Integer.toString(id));
    }
    return new ResponseEntity<>(projects.get(0), HttpStatus.OK);
  }

  /**
   * Returns the children of the given project.
   *
   * @param   id  Project whose children should be fetched.
   * @return  List of children. Never <code>null</code>.
   */
  @RequestMapping(value = "/{id}/children", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ProjectJson>> findChildren(@PathVariable int id) {
    return new ResponseEntity<>(projectService.findProjectChildren(id), HttpStatus.OK);
  }

  /**
   * Returns the parents of the given project.
   *
   * @param   id  Project whose parents should be fetched.
   * @return  List of parents. Never <code>null</code>. The immediate parent project is the first item and most grand parent project
   *          is the last item.
   */
  @RequestMapping(value = "/{id}/parents", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ProjectJson>> findParents(@PathVariable int id) {
    return new ResponseEntity<>(projectService.findProjectParents(id), HttpStatus.OK);
  }

  /**
   * Adds new project.
   *
   * @param   project Project to be inserted.
   * @return  Inserted project.
   */
  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION')")
  public ResponseEntity<ProjectJson> insert(@Valid @RequestBody ProjectJson project) {
    return new ResponseEntity<>(projectServiceComposer.insert(project), HttpStatus.OK);
  }

  /**
   * Update given project to database.
   *
   * @param   project Project to be updated.
   * @return  Updated project.
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ProjectJson> update(@PathVariable int id, @Valid @RequestBody ProjectJson project) {
    return new ResponseEntity<>(projectServiceComposer.update(id, project), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    final List<Integer> applicationIds = applicationServiceComposer.findApplicationsByProject(id).stream()
        .map(a -> a.getId()).collect(Collectors.toList());
    projectServiceComposer.delete(id, applicationIds);
    return new ResponseEntity<>(HttpStatus.OK);
  }


  /**
   * Find all applications for a project
   *
   * @param   id
   * @return  list of applications. Never <code>null</code>.
   */
  @RequestMapping(value = "/{id}/applications", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ApplicationJson>> findApplicationsByProject(@PathVariable int id) {
    List<ApplicationJson> applications = applicationServiceComposer.findApplicationsByProject(id);
    return new ResponseEntity<>(applications, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/applications", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<ApplicationJson>> addApplications(
      @PathVariable int id,
      @Valid @RequestBody(required = true) List<Integer> applicationIds) {
    return new ResponseEntity<>(projectServiceComposer.addApplications(id, applicationIds), HttpStatus.OK);
  }

  @RequestMapping(value = "/applications/{id}", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> removeApplication(@PathVariable int id) {
    projectServiceComposer.removeApplication(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Update parent of the given project.
   *
   * @param id              Project whose parent should be updated.
   * @param parentProject   New parent project.
   * @return Updated project.
   */
  @RequestMapping(value = {"/{id}/parentProject/{parentProject}", "/{id}/parentProject"}, method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ProjectJson> updateProjectParent(
      @PathVariable int id, @PathVariable Optional<Integer> parentProject) {
    return new ResponseEntity<>(projectServiceComposer.updateProjectParent(id, parentProject.orElse(null)), HttpStatus.OK);
  }

  /**
   * Remove parent from given projects.
   * @param projectIds Projects which parent should be removed
   * @return Updated project.
   */
  @RequestMapping(value = "/parent/remove", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ProjectJson> removeParent(@Valid @RequestBody(required = true) List<Integer> projectIds) {
    projectServiceComposer.updateParentForProjects(null, projectIds);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/history", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ChangeHistoryItemJson>> getChanges(@PathVariable int id) {
    return new ResponseEntity<>(projectService.getChanges(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/nextProjectNumber", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Integer> getNextProjectNumber() {
    return new ResponseEntity<>(projectService.getNextProjectNumber(), HttpStatus.OK);
  }
}
