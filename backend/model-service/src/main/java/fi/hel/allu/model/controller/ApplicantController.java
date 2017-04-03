package fi.hel.allu.model.controller;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.ApplicantDao;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.domain.Applicant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/applicants")
public class ApplicantController {

  @Autowired
  private ApplicantDao applicantDao;
  @Autowired
  private ApplicationDao applicationDao;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Applicant> findApplicant(@PathVariable int id) {
    Optional<Applicant> applicant = applicantDao.findById(id);
    Applicant applicantValue = applicant
        .orElseThrow(() -> new NoSuchEntityException("Applicant not found", Integer.toString(id)));
    return new ResponseEntity<>(applicantValue, HttpStatus.OK);
  }

  @RequestMapping(value = "/find", method = RequestMethod.POST)
  public ResponseEntity<List<Applicant>> findApplicants(@RequestBody List<Integer> ids) {
    return new ResponseEntity<>(applicantDao.findByIds(ids), HttpStatus.OK);
  }

  /**
   * Returns application ids of the applications having given applicant.
   *
   * @param id    id of the applicant whose related applications are returned.
   * @return  List of application ids. Never <code>null</code>.
   */
  @RequestMapping(value = "/applications/{id}", method = RequestMethod.GET)
  public ResponseEntity<List<Integer>> findApplicationsByApplicant(@PathVariable int id) {
    return new ResponseEntity<>(applicationDao.findByApplicant(id), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<Applicant>> findAllApplicants() {
    return new ResponseEntity<>(applicantDao.findAll(), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Applicant> updateApplicant(@PathVariable int id,
      @Valid @RequestBody(required = true) Applicant applicant) {
    Applicant resultApplicant = applicantDao.update(id, applicant);
    return new ResponseEntity<>(resultApplicant, resultApplicant != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Applicant> addApplicant(@Valid @RequestBody(required = true) Applicant applicant) {
    return new ResponseEntity<>(applicantDao.insert(applicant), HttpStatus.OK);
  }
}
