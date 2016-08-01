package fi.hel.allu.ui.controller;


import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.types.StatusType;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.LocationQueryJson;
import fi.hel.allu.ui.service.ApplicationService;
import fi.hel.allu.ui.service.SearchService;

@RestController
@RequestMapping("/applications")
public class ApplicationController {
  @Autowired
  private SearchService searchService;

  @Autowired
  private ApplicationService applicationService;

  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION')")
  public ResponseEntity<ApplicationJson> create(@Valid @RequestBody ApplicationJson applicationJson) {
    return new ResponseEntity<>(applicationService.createApplication(applicationJson), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> update(@PathVariable int id, @Valid @RequestBody(required = true) ApplicationJson
      applicationJson) {
    return new ResponseEntity<>(applicationService.updateApplication(id, applicationJson), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/status/handling", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> changeStatusToHandling(@PathVariable int id) {
    applicationService.changeStatus(id, StatusType.PENDING);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/status/decisionmaking", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> changeStatusToDecisionMaking(@PathVariable int id) {
    applicationService.changeStatus(id, StatusType.HANDLING);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/status/decision", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_DECISION')")
  public ResponseEntity<Void> changeStatusToDecision(@PathVariable int id) {
    applicationService.changeStatus(id, StatusType.DECISIONMAKING);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/status/supervision", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> changeStatusToSupervision(@PathVariable int id) {
    applicationService.changeStatus(id, StatusType.DECISION);
    return new ResponseEntity<>(HttpStatus.OK);
  }


  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<ApplicationJson> findByIdentifier(@PathVariable final String id) {
    return new ResponseEntity<>(applicationService.findApplicationById(id), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ApplicationJson>> findBy(@RequestParam(value = "handler") final String handlerId) {
    return new ResponseEntity<>(applicationService.findApplicationByHandler(handlerId), HttpStatus.OK);
  }

  @RequestMapping(value = "/search_location", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ApplicationJson>> findByLocation(@RequestBody final LocationQueryJson query) {
    return new ResponseEntity<>(applicationService.findApplicationByLocation(query), HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ApplicationJson>> searchAll(@RequestParam(value = "queryString") String queryString) {
    return new ResponseEntity<>(searchService.searchAll(queryString), HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ApplicationJson>> search(@Valid @RequestBody QueryParameters queryParameters) {
    return new ResponseEntity<>(searchService.search(queryParameters), HttpStatus.OK);
  }
}
