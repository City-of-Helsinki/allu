package fi.hel.allu.ui.controller;

import fi.hel.allu.ui.domain.ApplicantJson;
import fi.hel.allu.ui.service.ApplicantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for managing applicant information.
 */
@RestController
@RequestMapping("/applicants")
public class ApplicantController {

  @Autowired
  ApplicantService applicantService;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<ApplicantJson> findById(@PathVariable int id) {
    return new ResponseEntity<>(applicantService.findApplicantById(id), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ApplicantJson>> findAll() {
    return new ResponseEntity<>(applicantService.findAllApplicants(), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_DECISION')")
  public ResponseEntity<ApplicantJson> create(@Valid @RequestBody(required = true) ApplicantJson applicant) {
    return new ResponseEntity<>(applicantService.createApplicant(applicant), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_DECISION')")
  public ResponseEntity<ApplicantJson> update(@PathVariable int id, @Valid @RequestBody(required = true) ApplicantJson applicant) {
    return new ResponseEntity<>(applicantService.updateApplicant(id, applicant), HttpStatus.OK);
  }

  // TODO: delete/hide applicant                      DELETE /applicants/{id} ?
  // TODO: search incrementally applicants by name    POST /customers/applicants/search
}
