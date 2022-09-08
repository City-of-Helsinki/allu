package fi.hel.allu.external.maintenance.controller;

import fi.hel.allu.servicecore.service.SearchSyncService;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/search")
@Tag(name = "SearchSync", description = "Used only for maintenance")
public class SearchSyncController {
  private SearchSyncService searchSyncService;

  @Autowired
  public SearchSyncController(SearchSyncService searchSyncService) {
    this.searchSyncService = searchSyncService;
  }

  @RequestMapping(value = "/sync", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_SERVICE')")
  public ResponseEntity<Void> startSync() {
    searchSyncService.sync();
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
