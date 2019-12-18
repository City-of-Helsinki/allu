package fi.hel.allu.model.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.types.ChangeType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.ChangeHistoryItemInfo;
import fi.hel.allu.model.domain.changehistory.HistorySearchCriteria;

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
   * @param noReplaced parameter to control if full history with replaced applications is fetched or not
   * @return list of changes for the application
   */
  @RequestMapping(value = "/applications/{id}/history", method = RequestMethod.GET)
  public ResponseEntity<List<ChangeHistoryItem>> getChanges(@PathVariable int id, @RequestParam(required = false) Boolean noReplaced) {
    return ResponseEntity.ok(getHistory(id, noReplaced));
  }

  /**
   * Gets application status changes for external owner applications.
   *
   */
  @RequestMapping(value = "/externalowner/{externalownerid}/applications/history", method = RequestMethod.POST)
  public ResponseEntity<Map<Integer, List<ChangeHistoryItem>>> getChangeHistoryForExternalOwner(
      @PathVariable(value = "externalownerid") Integer externalOwnerId, @RequestBody HistorySearchCriteria searchCriteria) {
    Map<Integer, List<ChangeHistoryItem>> result = historyDao.getApplicationChangesForExternalOwner(externalOwnerId, searchCriteria);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  private List<ChangeHistoryItem> getHistory(int applicationId, Boolean noReplaced) {
    if (BooleanUtils.isTrue(noReplaced)) {
      return historyDao.getApplicationHistory(Arrays.asList(applicationId));
    } else {
      return historyDao.getApplicationHistory(applicationId);
    }
  }
}
