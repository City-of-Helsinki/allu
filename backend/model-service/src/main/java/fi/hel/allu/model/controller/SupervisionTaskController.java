package fi.hel.allu.model.controller;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.SupervisionTaskDao;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.common.domain.SupervisionTaskSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/supervisiontask")
public class SupervisionTaskController {

  private SupervisionTaskDao supervisionTaskDao;

  @Autowired
  public SupervisionTaskController(SupervisionTaskDao supervisionTaskDao) {
    this.supervisionTaskDao = supervisionTaskDao;
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<SupervisionTask> findById(@PathVariable int id) {
    return new ResponseEntity<>(
        supervisionTaskDao.findById(id).orElseThrow(() -> new NoSuchEntityException("Supervision task not found", Integer.toString(id))),
        HttpStatus.OK);
  }

  @RequestMapping(value = "/application/{applicationId}", method = RequestMethod.GET)
  public ResponseEntity<List<SupervisionTask>> findByApplicationId(@PathVariable int applicationId) {
    return new ResponseEntity<>(supervisionTaskDao.findByApplicationId(applicationId), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<SupervisionTask> update(@PathVariable int id, @Valid @RequestBody SupervisionTask supervisionTask) {
    supervisionTask.setId(id);
    return new ResponseEntity<>(supervisionTaskDao.update(supervisionTask), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<SupervisionTask> insert(@Valid @RequestBody SupervisionTask supervisionTask) {
    return new ResponseEntity<>(supervisionTaskDao.insert(supervisionTask), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable int id) {
    supervisionTaskDao.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<List<SupervisionTask>> search(@Valid @RequestBody SupervisionTaskSearchCriteria searchCriteria) {
    return new ResponseEntity<>(supervisionTaskDao.search(searchCriteria), HttpStatus.OK);
  }
}
