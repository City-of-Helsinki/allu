package fi.hel.allu.ui.controller;


import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.ApplicationListJson;
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
import java.util.List;

@RestController
@RequestMapping("/applications")
public class CreateApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION')")
    public ResponseEntity<List<ApplicationJson>> create(@Valid @RequestBody ApplicationListJson applicationListJson) {
        return new ResponseEntity<>(applicationService.createApplication(applicationListJson), HttpStatus.OK);
    }

}
