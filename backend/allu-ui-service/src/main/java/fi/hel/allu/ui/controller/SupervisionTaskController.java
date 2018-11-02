package fi.hel.allu.ui.controller;

import fi.hel.allu.common.domain.SupervisionTaskSearchCriteria;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionWorkItemJson;
import fi.hel.allu.servicecore.service.SupervisionTaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.time.ZonedDateTime;
import java.util.List;

import static org.springframework.format.annotation.DateTimeFormat.ISO.*;

@RestController
@RequestMapping("/supervisiontask")
public class SupervisionTaskController {

  @Autowired
  private SupervisionTaskService supervisionTaskService;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE','ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<SupervisionTaskJson> findById(@PathVariable int id) {
    return new ResponseEntity<>(supervisionTaskService.findById(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/application/{applicationId}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE','ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<SupervisionTaskJson>> findByApplicationId(@PathVariable int applicationId) {
    return new ResponseEntity<>(supervisionTaskService.findByApplicationId(applicationId), HttpStatus.OK);
  }

  @RequestMapping(value = "/location/{locationId}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE','ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<SupervisionTaskJson>> findByLocationId(@PathVariable int locationId) {
    return new ResponseEntity<>(supervisionTaskService.findByLocationId(locationId), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<SupervisionTaskJson> update(@PathVariable int id, @Valid @RequestBody SupervisionTaskJson supervisionTask) {
    supervisionTask.setId(id);
    return new ResponseEntity<>(supervisionTaskService.update(supervisionTask), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE','ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<SupervisionTaskJson> insert(@Valid @RequestBody SupervisionTaskJson supervisionTask) {
    return new ResponseEntity<>(supervisionTaskService.insert(supervisionTask), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE','ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    supervisionTaskService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<Page<SupervisionWorkItemJson>> search(
      @Valid @RequestBody SupervisionTaskSearchCriteria searchCriteria, Pageable pageRequest) {
    return new ResponseEntity<>(supervisionTaskService.search(searchCriteria, pageRequest), HttpStatus.OK);
  }

  @RequestMapping(value = "/owner/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION', 'ROLE_SUPERVISE')")
  public ResponseEntity<Void> updateOwner(@PathVariable int id, @RequestBody(required = true) List<Integer> taskIds) {
    supervisionTaskService.updateOwner(id, taskIds);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/owner/remove", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION', 'ROLE_SUPERVISE')")
  public ResponseEntity<Void> removeOwner(@RequestBody(required = true) List<Integer> taskIds) {
    supervisionTaskService.removeOwner(taskIds);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/approve", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<SupervisionTaskJson> approve(@PathVariable int id, @Valid @RequestBody SupervisionTaskJson supervisionTask) {
    supervisionTask.setId(id);
    return new ResponseEntity<>(supervisionTaskService.approve(supervisionTask), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/reject", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<SupervisionTaskJson> reject(
      @PathVariable int id,
      @Valid @RequestBody SupervisionTaskJson supervisionTask,
      @RequestParam(value = "newDate", required = true) @DateTimeFormat(iso = DATE_TIME) ZonedDateTime newDate) {
    supervisionTask.setId(id);
    return new ResponseEntity<>(supervisionTaskService.reject(supervisionTask, newDate), HttpStatus.OK);
  }
}
