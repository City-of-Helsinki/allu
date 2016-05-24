package fi.hel.allu.ui.controller;

import fi.hel.allu.ui.service.ApplicationService;
import fi.hel.allu.ui.domain.ApplicationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
public class FindApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('ROLE_VIEW')")
    public ResponseEntity<ApplicationDTO> findByIdentifier(@PathVariable final String id) {
        return new ResponseEntity<ApplicationDTO>(applicationService.findApplicationById(id), HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('ROLE_VIEW')")
    public ResponseEntity<ApplicationDTO> findBy(@RequestParam(value = "handler") final String handlerId) {
        return new ResponseEntity<ApplicationDTO>(applicationService.findApplicationByHandler(handlerId), HttpStatus.OK);
    }
}

