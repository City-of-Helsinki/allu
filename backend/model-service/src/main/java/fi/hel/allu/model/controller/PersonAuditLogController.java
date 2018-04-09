package fi.hel.allu.model.controller;

import fi.hel.allu.model.dao.PersonAuditLogDao;
import fi.hel.allu.model.domain.PersonAuditLogLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/personauditlog")
public class PersonAuditLogController {

  private final PersonAuditLogDao personAuditLogDao;

  @Autowired
  public PersonAuditLogController(PersonAuditLogDao personAuditLogDao) {
    this.personAuditLogDao = personAuditLogDao;
  }

  @RequestMapping(value = "/log", method = RequestMethod.POST)
  public ResponseEntity<Void> log(@Valid @RequestBody PersonAuditLogLog logEntry) {
    personAuditLogDao.insert(logEntry);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
