package fi.hel.allu.ui.controller;

import fi.hel.allu.ui.service.ApplicationService;
import fi.hel.allu.ui.domain.ApplicationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FindApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @RequestMapping(value = "/applications/findbyid/{identifier}", method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('ROLE_VIEW')")
    public ResponseEntity<ApplicationDTO> findByIdentifier(@PathVariable final String identifier) {
        return new ResponseEntity<ApplicationDTO>(applicationService.findApplicationById(identifier), HttpStatus.OK);
    }
}