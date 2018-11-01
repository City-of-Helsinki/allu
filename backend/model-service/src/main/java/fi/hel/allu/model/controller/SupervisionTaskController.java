package fi.hel.allu.model.controller;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.SupervisionTaskSearchCriteria;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.model.service.SupervisionTaskService;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@RestController
@RequestMapping("/supervisiontask")
public class SupervisionTaskController {

  private SupervisionTaskService supervisionTaskService;

  @Autowired
  public SupervisionTaskController(SupervisionTaskService supervisionTaskService) {
    this.supervisionTaskService = supervisionTaskService;
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<SupervisionTask> findById(@PathVariable int id) {
    return new ResponseEntity<>(supervisionTaskService.findById(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/application/{applicationId}", method = RequestMethod.GET)
  public ResponseEntity<List<SupervisionTask>> findByApplicationId(@PathVariable int applicationId) {
    return new ResponseEntity<>(supervisionTaskService.findByApplicationId(applicationId), HttpStatus.OK);
  }

  @RequestMapping(value = "/application/{applicationId}/type/{type}", method = RequestMethod.GET)
  public ResponseEntity<List<SupervisionTask>> findByApplicationIdAndType(
      @PathVariable(value = "applicationId") int applicationId,
      @PathVariable(value = "type") SupervisionTaskType type) {
    return new ResponseEntity<>(supervisionTaskService.findByApplicationIdAndType(applicationId, type), HttpStatus.OK);
  }


  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<SupervisionTask> insert(@Valid @RequestBody SupervisionTask supervisionTask) {
    return new ResponseEntity<>(supervisionTaskService.insert(supervisionTask), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<SupervisionTask> update(@PathVariable int id, @Valid @RequestBody SupervisionTask supervisionTask) {
    supervisionTask.setId(id);
    return new ResponseEntity<>(supervisionTaskService.update(id, supervisionTask), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable int id) {
    supervisionTaskService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<Page<SupervisionTask>> search(@Valid @RequestBody SupervisionTaskSearchCriteria searchCriteria,
      Pageable pageRequest) {
    return new ResponseEntity<>(supervisionTaskService.search(searchCriteria, pageRequest), HttpStatus.OK);
  }

  @RequestMapping(value = "/owner/{ownerId}", method = RequestMethod.PUT)
  public ResponseEntity<Void> updateOwner(@PathVariable int ownerId, @RequestBody List<Integer> tasks) {
    supervisionTaskService.updateOwner(ownerId, tasks);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/owner/remove", method = RequestMethod.PUT)
  public ResponseEntity<Void> removeOwner(@RequestBody List<Integer> tasks) {
    supervisionTaskService.removeOwner(tasks);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/approve", method = RequestMethod.PUT)
  public ResponseEntity<SupervisionTask> approve(@PathVariable int id, @Valid @RequestBody SupervisionTask supervisionTask) {
    supervisionTask.setId(id);
    return new ResponseEntity<>(supervisionTaskService.approve(supervisionTask), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/reject", method = RequestMethod.PUT)
  public ResponseEntity<SupervisionTask> reject(
      @PathVariable int id,
      @Valid @RequestBody SupervisionTask supervisionTask,
      @RequestParam(value = "newDate") @DateTimeFormat(iso = DATE_TIME) ZonedDateTime newDate) {
    supervisionTask.setId(id);
    return new ResponseEntity<>(supervisionTaskService.reject(supervisionTask, newDate), HttpStatus.OK);
  }

  @RequestMapping(value = "/externalowner/{externalownerid}/history", method = RequestMethod.POST)
  public ResponseEntity<Map<Integer, List<SupervisionTask>>> getSupervisionTaskHistoryForExternalOwner(
      @PathVariable(value = "externalownerid") Integer externalOwnerId, @RequestParam(value = "eventsafter") @DateTimeFormat(iso = DATE_TIME) ZonedDateTime eventsAfter, @RequestBody List<Integer> includedApplicationIds) {
    Map<Integer, List<SupervisionTask>> result = supervisionTaskService.getSupervisionTaskHistoryForExternalOwner(externalOwnerId, eventsAfter, includedApplicationIds);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

}
