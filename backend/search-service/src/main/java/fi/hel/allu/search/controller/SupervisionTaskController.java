package fi.hel.allu.search.controller;

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

@RestController
@RequestMapping("/supervisiontask")
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
}