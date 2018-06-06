package fi.hel.allu.model.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.MailSenderLog;
import fi.hel.allu.model.dao.LogDao;

@RestController
@RequestMapping("/logs")
public class LogController {

  private final LogDao logDao;

  @Autowired
  public LogController(LogDao logDao) {
    this.logDao = logDao;
  }

  @RequestMapping(value = "/mailsender", method = RequestMethod.POST)
  public ResponseEntity<Integer> insert(@RequestBody MailSenderLog log) {
    return ResponseEntity.ok(logDao.insert(log));
  }
}
