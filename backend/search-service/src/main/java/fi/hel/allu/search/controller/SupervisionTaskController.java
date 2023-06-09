package fi.hel.allu.search.controller;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.domain.SupervisionWorkItem;
import fi.hel.allu.search.domain.ApplicationQueryParameters;
import fi.hel.allu.search.service.SupervisionTaskSearchService;
import fi.hel.allu.search.util.Constants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/supervisiontasks")
public class SupervisionTaskController {

    private final SupervisionTaskSearchService supervisionTaskSearchService;

    public SupervisionTaskController(SupervisionTaskSearchService supervisionTaskSearchService) {
        this.supervisionTaskSearchService = supervisionTaskSearchService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody SupervisionWorkItem supervisionWorkItem) {
        supervisionTaskSearchService.insert(supervisionWorkItem);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/search")
    public ResponseEntity<Page<SupervisionWorkItem>> search(@Valid @RequestBody ApplicationQueryParameters queryParameters,
                                                      @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.DEFAULT_PAGE_SIZE)
                                                      Pageable pageRequest,
                                                      @RequestParam(defaultValue = "false") Boolean matchAny) {
        return new ResponseEntity<>(supervisionTaskSearchService.findSupervisionTaskByField(queryParameters, pageRequest, matchAny), HttpStatus.OK);
    }

    @PostMapping(value = "/sync/data")
    public ResponseEntity<Void> syncData(@Valid @RequestBody List<SupervisionWorkItem> supervisionWorkItems) {
        supervisionTaskSearchService.syncData(supervisionWorkItems);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/update/status")
    public ResponseEntity<Void> updateStatus(@RequestParam Integer applicationId, @Valid @RequestBody StatusType status) {
        supervisionTaskSearchService.updateApplicationStatus(applicationId, status);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}