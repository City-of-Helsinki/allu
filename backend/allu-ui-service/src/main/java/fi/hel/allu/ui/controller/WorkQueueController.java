package fi.hel.allu.ui.controller;

import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ApplicationQueryParameters;
import fi.hel.allu.servicecore.service.WorkQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Controller for different workqueue related interactions.
 */
@RestController
@RequestMapping("/workqueue")
public class WorkQueueController {
  @Autowired
  private WorkQueueService workQueueService;

  @PostMapping
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<Page<ApplicationES>> searchSharedByGroup(
      @Valid @RequestBody ApplicationQueryParameters queryParameters,
      @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.DEFAULT_PAGE_SIZE, sort = "creationTime", direction = Direction.DESC)
      Pageable pageRequest) {
    return new ResponseEntity<>(workQueueService.searchSharedByGroup(queryParameters, pageRequest), HttpStatus.OK);
  }
}