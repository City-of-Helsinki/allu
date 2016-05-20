package fi.hel.allu.model.controller;

import java.util.Optional;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.model.dao.ProjectDao;
import fi.hel.allu.model.domain.Project;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Inject
    private ProjectDao projectDao;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Project> find(@PathVariable int id) {
        return responseEntity(projectDao.findById(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Project> update(@PathVariable int id, @RequestBody(required = true) Project project) {
        return responseEntity(projectDao.update(id, project));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Project> insert(@RequestBody(required = true) Project project) {
        if (project.getProjectId() != null) {
            throw new IllegalArgumentException("Id must be null for insert");
        }
        return responseEntity(projectDao.insert(project));
    }

    private ResponseEntity<Project> responseEntity(Optional<Project> project) {
        if (project.isPresent()) {
            return new ResponseEntity<Project>(project.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<Project>(HttpStatus.NOT_FOUND);
        }
    }
}
