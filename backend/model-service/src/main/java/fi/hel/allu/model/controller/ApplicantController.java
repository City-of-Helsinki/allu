package fi.hel.allu.model.controller;

import fi.hel.allu.model.dao.ApplicantDao;
import fi.hel.allu.model.domain.Applicant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/applicants")
public class ApplicantController {

    @Autowired
    private ApplicantDao applicantDao;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Applicant> applicant(@PathVariable int id) {
        Optional<Applicant> applicant = applicantDao.findById(id);
        if (applicant.isPresent()) {
            return new ResponseEntity<>(applicant.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Applicant> updateApplicant(@PathVariable int id, @RequestBody(required = true) Applicant applicant) {
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
