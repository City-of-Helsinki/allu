package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.changehistory.HistorySearchCriteria;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class ApplicationHistoryController {

  private final HistoryDao historyDao;

  @Autowired
  public ApplicationHistoryController(HistoryDao historyDao) {
    this.historyDao = historyDao;
  }

  /**
   * Add an application history entry
   * @param id The application's database ID
   * @param change the change item to add
   */
  @PostMapping(value = "/applications/{id}/history")
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
  @GetMapping(value = "/applications/{id}/history")
  public ResponseEntity<List<ChangeHistoryItem>> getChanges(@PathVariable int id, @RequestParam(required = false) Boolean noReplaced) {
    return ResponseEntity.ok(getHistory(id, noReplaced));
  }

  /**
   * Gets application status changes for external owner applications.
   *
   */
  @PostMapping(value = "/externalowner/{externalownerid}/applications/history")
  public ResponseEntity<Map<Integer, List<ChangeHistoryItem>>> getChangeHistoryForExternalOwner(
      @PathVariable(value = "externalownerid") Integer externalOwnerId, @RequestBody HistorySearchCriteria searchCriteria) {
    Map<Integer, List<ChangeHistoryItem>> result = historyDao.getApplicationChangesForExternalOwner(externalOwnerId, searchCriteria);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @GetMapping(value = "/applications/{id}/history/hasstatus/{status}")
  public ResponseEntity<Boolean> hasStatusInHistory(@PathVariable int id, @PathVariable StatusType status) {
    return ResponseEntity.ok(historyDao.applicationHasStatusInHistory(id, status));
  }

  private List<ChangeHistoryItem> getHistory(int applicationId, Boolean noReplaced) {
    if (BooleanUtils.isTrue(noReplaced)) {
      return historyDao.getApplicationHistory(Arrays.asList(applicationId));
    } else {
      return historyDao.getApplicationHistory(applicationId);
    }
  }
}