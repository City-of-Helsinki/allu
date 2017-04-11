package fi.hel.allu.ui.controller;

import fi.hel.allu.ui.domain.ApplicantJson;
import fi.hel.allu.ui.domain.ApplicantWithContactsJson;
import fi.hel.allu.ui.domain.ContactJson;
import fi.hel.allu.ui.domain.QueryParametersJson;
import fi.hel.allu.ui.service.ApplicantService;
import fi.hel.allu.ui.service.ContactService;
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
  @Autowired
  ContactService contactService;

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

  @RequestMapping(value = "/applicant/{id}/contacts", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ContactJson>> findByApplicant(@PathVariable int id) {
    return new ResponseEntity<>(contactService.findByApplicant(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ApplicantJson>> search(@Valid @RequestBody QueryParametersJson queryParameters) {
    return new ResponseEntity<>(applicantService.search(queryParameters), HttpStatus.OK);
  }

  @RequestMapping(value = "/search/{fieldName}", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ApplicantJson>> search(
      @PathVariable String fieldName,
      @RequestBody String searchString) {
    return new ResponseEntity<>(applicantService.searchPartial(fieldName, searchString), HttpStatus.OK);
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

  @RequestMapping(value = "/withcontacts", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_DECISION')")
  public ResponseEntity<ApplicantWithContactsJson> createWithContacts(@Valid @RequestBody ApplicantWithContactsJson applicantWithContactsJson) {
    return new ResponseEntity<>(applicantService.createApplicantWithContacts(applicantWithContactsJson), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/withcontacts", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_DECISION')")
  public ResponseEntity<ApplicantWithContactsJson> updateWithContacts(
      @Valid @RequestBody ApplicantWithContactsJson applicantWithContactsJson) {
    return new ResponseEntity<>(applicantService.updateApplicantWithContacts(applicantWithContactsJson), HttpStatus.OK);
  }


  // TODO: delete/hide applicant                      DELETE /applicants/{id} ?
}
