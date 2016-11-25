package fi.hel.allu.model.controller;

import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Project;
import fi.hel.allu.model.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

  @Autowired
  private ApplicationDao applicationDao;
  @Autowired
  private ProjectService projectService;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Project> find(@PathVariable int id) {
    return new ResponseEntity<Project>(projectService.find(id), HttpStatus.OK);
  }

  /**
   * TODO: REMOVE THIS FUNCTION AS SOON AS PROPER PROJECT SEARCH IS IMPLEMENTED
   */
  @RequestMapping(value = "/all", method = RequestMethod.GET)
  public List<Project> findAllProjects() {
    return projectService.findAllProjects();
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
   * Insert given project to database.
   *
   * @param project Project to be inserted.
   * @return Inserted project.
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Project> insert(@Valid @RequestBody(required = true) Project project) {
    return new ResponseEntity<Project>(projectService.insert(project), HttpStatus.OK);
  }

  /**
   * Update given project to database.
   *
   * @param project Project to be updated.
   * @return Updated project.
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Project> update(@PathVariable int id, @Valid @RequestBody(required = true) Project project) {
    return new ResponseEntity<Project>(projectService.update(id, project), HttpStatus.OK);
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

  /**
   * Updates applications of given project to the given set of applications.
   *
   * @param id             Id of the project.
   * @param applicationIds Application ids to be attached to the given project.
   * @return Nothing.
   */
  @RequestMapping(value = "/{id}/applications", method = RequestMethod.PUT)
  public ResponseEntity<Project> updateProjectApplications(
      @PathVariable int id, @Valid @RequestBody(required = true) List<Integer> applicationIds) {
    return new ResponseEntity<>(projectService.updateProjectApplications(id, applicationIds), HttpStatus.OK);
  }

  /**
   * Update parent of the given project.
   *
   * @param id            Project whose parent should be updated.
   * @param parentProject New parent project.
   * @return Updated project with new parent.
   */
  @RequestMapping(value = "/{id}/parentProject", method = RequestMethod.PUT)
  public ResponseEntity<Project> updateProjectParent(
      @PathVariable int id, @Valid @RequestBody(required = true) Integer parentProject) {
    return new ResponseEntity<>(projectService.updateProjectParent(id, parentProject), HttpStatus.OK);
  }


  /**
   * Updates the information of given projects by going through the project / application hierarchy to find the up-to-date values. Updates
   * also all projects related to the given projects.
   *
   * @param projectIds List of projects to be updated.
   * @return Projects that have been updated. May contain more projects than in the list provided as a parameter.
   */
  @RequestMapping(value = "/update", method = RequestMethod.PUT)
  public ResponseEntity<List<Project>> updateProjectInformation(@Valid @RequestBody(required = true) List<Integer> projectIds) {
    return new ResponseEntity<>(projectService.updateProjectInformation(projectIds), HttpStatus.OK);
  }
}
