package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.SupervisionTaskSearchCriteria;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.model.domain.SupervisionWorkItem;
import fi.hel.allu.model.service.SupervisionTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@RestController
@RequestMapping("/supervisiontask")
public class SupervisionTaskController {

  private final SupervisionTaskService supervisionTaskService;

  @Autowired
  public SupervisionTaskController(SupervisionTaskService supervisionTaskService) {
    this.supervisionTaskService = supervisionTaskService;
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<SupervisionTask> findById(@PathVariable int id) {
    return new ResponseEntity<>(supervisionTaskService.findById(id), HttpStatus.OK);
  }

  @GetMapping(value = "/application/{applicationId}")
  public ResponseEntity<List<SupervisionTask>> findByApplicationId(@PathVariable int applicationId) {
    return new ResponseEntity<>(supervisionTaskService.findByApplicationId(applicationId), HttpStatus.OK);
  }

  @GetMapping(value = "/location/{locationId}")
  public ResponseEntity<List<SupervisionTask>> findByLocationId(@PathVariable int locationId) {
    return new ResponseEntity<>(supervisionTaskService.findByLocationId(locationId), HttpStatus.OK);
  }

  @GetMapping(value = "/application/{applicationId}/type/{type}")
  public ResponseEntity<List<SupervisionTask>> findByApplicationIdAndType(
      @PathVariable(value = "applicationId") int applicationId,
      @PathVariable(value = "type") SupervisionTaskType type,
      @RequestParam(name = "locationId", required = false) Integer locationId) {
    if (locationId != null) {
      return ResponseEntity.ok(supervisionTaskService.findByApplicationIdAndTypeAndLocation(applicationId, type, locationId));
    } else {
      return ResponseEntity.ok(supervisionTaskService.findByApplicationIdAndType(applicationId, type));
    }
  }

  /**
   * Find all SupervisionTasks, with paging support
   *
   * @param pageRequest page request for the search
   */
  @GetMapping (value = "/all")
  public ResponseEntity<Page<SupervisionWorkItem>> findAll(
          @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.DEFAULT_PAGE_SIZE)
          Pageable pageRequest) {
    return new ResponseEntity<>(supervisionTaskService.findAll(pageRequest), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<SupervisionTask> insert(@Valid @RequestBody SupervisionTask supervisionTask) {
    return new ResponseEntity<>(supervisionTaskService.insert(supervisionTask), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}")
  public ResponseEntity<SupervisionTask> update(@PathVariable int id, @Valid @RequestBody SupervisionTask supervisionTask) {
    supervisionTask.setId(id);
    return new ResponseEntity<>(supervisionTaskService.update(id, supervisionTask), HttpStatus.OK);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    supervisionTaskService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping(value = "/{id}/workitem")
  public ResponseEntity<SupervisionWorkItem> getWorkItem(@PathVariable int id) {
    return new ResponseEntity<>(supervisionTaskService.getWorkItem(id), HttpStatus.OK);
  }

  @PutMapping(value = "/owner/{ownerId}")
  public ResponseEntity<Void> updateOwner(@PathVariable int ownerId, @RequestBody List<Integer> tasks) {
    supervisionTaskService.updateOwner(ownerId, tasks);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping(value = "/owner/remove")
  public ResponseEntity<Void> removeOwner(@RequestBody List<Integer> tasks) {
    supervisionTaskService.removeOwner(tasks);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/approve")
  public ResponseEntity<SupervisionTask> approve(@PathVariable int id, @Valid @RequestBody SupervisionTask supervisionTask) {
    supervisionTask.setId(id);
    return new ResponseEntity<>(supervisionTaskService.approve(supervisionTask), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/reject")
  public ResponseEntity<SupervisionTask> reject(
      @PathVariable int id,
      @Valid @RequestBody SupervisionTask supervisionTask,
      @RequestParam(value = "newDate") @DateTimeFormat(iso = DATE_TIME) ZonedDateTime newDate) {
    supervisionTask.setId(id);
    return new ResponseEntity<>(supervisionTaskService.reject(supervisionTask, newDate), HttpStatus.OK);
  }

  @PostMapping(value = "/externalowner/{externalownerid}/history")
  public ResponseEntity<Map<Integer, List<SupervisionTask>>> getSupervisionTaskHistoryForExternalOwner(
      @PathVariable(value = "externalownerid") Integer externalOwnerId, @RequestParam(value = "eventsafter") @DateTimeFormat(iso = DATE_TIME) ZonedDateTime eventsAfter, @RequestBody List<Integer> includedExternalApplicationIds) {
    Map<Integer, List<SupervisionTask>> result = supervisionTaskService.getSupervisionTaskHistoryForExternalOwner(externalOwnerId, eventsAfter, includedExternalApplicationIds);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @GetMapping(value = "/{id}/address")
  public ResponseEntity<String[]> findAddressById(@PathVariable int id) {
    return ResponseEntity.ok(supervisionTaskService.findAddressById(id));
  }

  @GetMapping(value = "/{applicationId}/count")
  public ResponseEntity<List<Integer>> getSupervisionTaskCount(@PathVariable int applicationId){
    return ResponseEntity.ok(supervisionTaskService.getSupervisionTaskCount(applicationId));
  }
}
