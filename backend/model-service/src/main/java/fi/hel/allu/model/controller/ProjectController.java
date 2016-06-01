package fi.hel.allu.model.controller;

import fi.hel.allu.model.dao.ProjectDao;
import fi.hel.allu.model.domain.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectDao projectDao;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Project> find(@PathVariable int id) {
        Optional<Project> project = projectDao.findById(id);
        if (project.isPresent()) {
            return new ResponseEntity<Project>(project.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<Project>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Project> update(@PathVariable int id, @RequestBody(required = true) Project project) {
        return new ResponseEntity<Project>(projectDao.update(id, project), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Project> insert(@RequestBody(required = true) Project project) {
        if (project.getId() != null) {
            throw new IllegalArgumentException("Id must be null for insert");
        }
        return new ResponseEntity<Project>(projectDao.insert(project), HttpStatus.OK);
    }
}
