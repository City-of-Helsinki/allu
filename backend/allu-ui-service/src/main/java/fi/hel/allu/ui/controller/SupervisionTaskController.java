package fi.hel.allu.ui.controller;

import fi.hel.allu.common.domain.SupervisionTaskSearchCriteria;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionWorkItemJson;
import fi.hel.allu.servicecore.service.SupervisionTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
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
  public ResponseEntity<List<SupervisionWorkItemJson>> search(@Valid @RequestBody SupervisionTaskSearchCriteria searchCriteria) {
    return new ResponseEntity<>(supervisionTaskService.search(searchCriteria), HttpStatus.OK);
  }
}
