package fi.hel.allu.ui.controller;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ProjectJson;
import fi.hel.allu.servicecore.domain.QueryParametersJson;
import fi.hel.allu.ui.service.ApplicationServiceComposer;
import fi.hel.allu.ui.service.ProjectService;
import fi.hel.allu.ui.service.ProjectServiceComposer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
  public ResponseEntity<List<ProjectJson>> search(@Valid @RequestBody QueryParametersJson queryParameters) {
    return new ResponseEntity<>(projectServiceComposer.search(queryParameters), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<ProjectJson> findById(@PathVariable int id) {
    List<ProjectJson> projects = projectService.findByIds(Collections.singletonList(id));
    if (projects.size() != 1) {
      throw new NoSuchEntityException("Project not found", Integer.toString(id));
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
  public ResponseEntity<ProjectJson> insert(@Valid @RequestBody(required = true) ProjectJson project) {
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
  public ResponseEntity<ProjectJson> update(@PathVariable int id, @Valid @RequestBody(required = true) ProjectJson project) {
    return new ResponseEntity<>(projectServiceComposer.update(id, project), HttpStatus.OK);
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

  /**
   * Updates applications of given project to the given set of applications.
   *
   * @param id              Id of the project.
   * @param applicationIds  Application ids to be attached to the given project. Use empty list to clear all references to the given project.
   */
  @RequestMapping(value = "/{id}/applications", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ProjectJson> updateProjectApplications(
      @PathVariable int id, @Valid @RequestBody(required = true) List<Integer> applicationIds) {
    return new ResponseEntity<>(projectServiceComposer.updateProjectApplications(id, applicationIds), HttpStatus.OK);
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
}
