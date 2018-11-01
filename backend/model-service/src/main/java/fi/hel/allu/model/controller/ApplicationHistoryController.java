package fi.hel.allu.model.controller;

import fi.hel.allu.common.types.ChangeType;
import fi.hel.allu.model.dao.ApplicationDao;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.ChangeHistoryItemInfo;

import static org.springframework.format.annotation.DateTimeFormat.ISO.*;

@RestController
public class ApplicationHistoryController {

  private final HistoryDao historyDao;
  private final ApplicationDao applicationDao;

  @Autowired
  public ApplicationHistoryController(HistoryDao historyDao, ApplicationDao applicationDao) {
    this.historyDao = historyDao;
    this.applicationDao = applicationDao;
  }


  /**
   * Add an application history entry
   * @param id The application's database ID
   * @param change the change item to add
   */
  @RequestMapping(value = "/applications/{id}/history", method = RequestMethod.POST)
  public ResponseEntity<Void> addChange(@PathVariable int id, @RequestBody ChangeHistoryItem change) {
    historyDao.addApplicationChange(id, change);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Get application history
   *
   * @param id the application's database ID
   * @return list of changes for the application
   */
  @RequestMapping(value = "/applications/{id}/history", method = RequestMethod.GET)
  public ResponseEntity<List<ChangeHistoryItem>> getChanges(@PathVariable int id) {
    final List<ChangeHistoryItem> history = historyDao.getApplicationHistory(id);
    history.stream().forEach(item -> {
      final ChangeHistoryItemInfo info = item.getInfo();
      if (info.getId() != null && item.getChangeType() == ChangeType.STATUS_CHANGED) {
          final Application app = applicationDao.findById(item.getInfo().getId());
          info.setApplicationId(app.getApplicationId());
          info.setName(app.getName());
      }
    });
    return ResponseEntity.ok(history);
  }

  /**
   * Gets application status changes for external owner applications.
   *
   */
  @RequestMapping(value = "/externalowner/{externalownerid}/applications/history", method = RequestMethod.POST)
  public ResponseEntity<Map<Integer, List<ChangeHistoryItem>>> getApplicationStatusChangesForExternalOwner(
      @PathVariable(value = "externalownerid") Integer externalOwnerId, @RequestParam(value = "eventsafter") @DateTimeFormat(iso = DATE_TIME) ZonedDateTime eventsAfter, @RequestBody List<Integer> includedApplicationIds) {
    Map<Integer, List<ChangeHistoryItem>> result = historyDao.getApplicationStatusChangesForExternalOwner(externalOwnerId, eventsAfter, includedApplicationIds);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

}
