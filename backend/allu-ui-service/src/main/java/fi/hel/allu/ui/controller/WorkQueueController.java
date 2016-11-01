package fi.hel.allu.ui.controller;

import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.QueryParametersJson;
import fi.hel.allu.ui.service.WorkQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for different workqueue related interactions.
 */
@RestController
@RequestMapping("/workqueue")
public class WorkQueueController {
  @Autowired
  private WorkQueueService workQueueService;

  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ApplicationJson>> searchSharedByGroup(@Valid @RequestBody QueryParametersJson queryParameters) {
    return new ResponseEntity<>(workQueueService.searchSharedByGroup(queryParameters), HttpStatus.OK);
  }
}
