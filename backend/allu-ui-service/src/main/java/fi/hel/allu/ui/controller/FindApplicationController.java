package fi.hel.allu.ui.controller;


import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.LocationQueryJson;
import fi.hel.allu.ui.service.ApplicationService;
import fi.hel.allu.ui.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/applications")
public class FindApplicationController {

  @Autowired
  private ApplicationService applicationService;
  @Autowired
  private SearchService searchService;

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

