package fi.hel.allu.model.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.ApplicantDao;
import fi.hel.allu.model.domain.Applicant;

@RestController
@RequestMapping("/applicants")
public class ApplicantController {

  @Autowired
  private ApplicantDao applicantDao;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Applicant> applicant(@PathVariable int id) {
    Optional<Applicant> applicant = applicantDao.findById(id);
    Applicant applicantValue = applicant
        .orElseThrow(() -> new NoSuchEntityException("Applicant not found", Integer.toString(id)));
    return new ResponseEntity<>(applicantValue, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Applicant> updateApplicant(@PathVariable int id,
      @RequestBody(required = true) Applicant applicant) {
    Applicant resultApplicant = applicantDao.update(id, applicant);
    return new ResponseEntity<>(resultApplicant, resultApplicant != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Applicant> addApplicant(@RequestBody(required = true) Applicant applicant) {
    if (applicant.getId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    return new ResponseEntity<>(applicantDao.insert(applicant), HttpStatus.OK);
  }
}
