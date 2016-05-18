package fi.hel.allu.ui.controller;


import fi.hel.allu.ui.domain.ApplicationDTO;
import fi.hel.allu.ui.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class CreateApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @RequestMapping(value = "/applications/create", method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION')")
    public ResponseEntity<ApplicationDTO> create(@RequestBody @Valid ApplicationDTO applicationDTO) {
        return new ResponseEntity<ApplicationDTO>(applicationService.createApplication(applicationDTO), HttpStatus.OK);
    }

}
