package fi.hel.allu.external.controller.maintenance;

import fi.hel.allu.servicecore.service.SearchSyncService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/search")
@Tag(name = "SearchSync", description = "Used only for maintenance")
public class SearchSyncController {

    private final SearchSyncService searchSyncService;

    public SearchSyncController(SearchSyncService searchSyncService) {
        this.searchSyncService = searchSyncService;
    }

    @PostMapping(value = "/sync")
    @PreAuthorize("hasAnyRole('ROLE_SERVICE')")
    public ResponseEntity<Void> startSync() {
        searchSyncService.sync();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}