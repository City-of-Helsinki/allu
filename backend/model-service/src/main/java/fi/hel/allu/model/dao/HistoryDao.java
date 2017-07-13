package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.FieldChange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QChangeHistory.changeHistory;
import static fi.hel.allu.QFieldChange.fieldChange;

/**
 * The DAO class for handling application history
 */
@Repository
public class HistoryDao {
  @Autowired
  private SQLQueryFactory queryFactory;

  private final QBean<FieldChange> fieldChangeBean = bean(FieldChange.class,
      fieldChange.all());

  /**
   * Get application's change history
   *
   * @param applicationId
   *          application's database ID
   * @return list of changes, ordered from oldest to newest
   */
  @Transactional(readOnly = true)
  public List<ChangeHistoryItem> getApplicationHistory(int applicationId) {
    return getChangeHistory(changeHistory.applicationId.eq(applicationId));
  }

  /**
   * Get customer's change history
   *
   * @param applicationId application's database ID
   * @return list of changes, ordered from oldest to newest
   */
  @Transactional(readOnly = true)
  public List<ChangeHistoryItem> getCustomerHistory(int customerId) {
    return getChangeHistory(changeHistory.customerId.eq(customerId));
  }

  /*
   * Get the change history items that match the given condition.
   */
  private List<ChangeHistoryItem> getChangeHistory(Predicate condition) {
    List<Tuple> results = queryFactory.select(changeHistory.all()).from(changeHistory)
        .where(condition).orderBy(changeHistory.id.asc()).fetch();
    return results.stream()
        .map(r -> new ChangeHistoryItem(r.get(changeHistory.userId), r.get(changeHistory.changeType),
            r.get(changeHistory.newStatus), r.get(changeHistory.changeTime), getChangeLines(r.get(changeHistory.id))))
        .collect(Collectors.toList());
  }

  /*
   * Get all field changes for the given change history item.
   */
  private List<FieldChange> getChangeLines(Integer changeId) {
    return queryFactory.select(fieldChangeBean).from(fieldChange).where(fieldChange.changeHistoryId.eq(changeId))
        .fetch();
  }

  /**
   * Add a change to application
   *
   * @param applicationId
   *          application's database ID
   * @param change
   *          the change item to append to application's change list.
   */
  @Transactional
  public void addApplicationChange(int applicationId, ChangeHistoryItem change) {
    addChangeWithKey(changeHistory.applicationId, applicationId, change);
  }

  /**
   * Add a change to customer
   *
   * @param customerId customer's database ID
   * @param change the change item to append to customer's change list.
   */
  @Transactional
  public void addCustomerChange(int customerId, ChangeHistoryItem change) {
    addChangeWithKey(changeHistory.customerId, customerId, change);
  }

  /*
   * Generic way to add a change item. Use the keyPath to choose which history
   * to operate with: use changeHistory.applicationId for application changes
   * and changeHistory.customerId for customer changes.
   */
  private void addChangeWithKey(Path<Integer> keyPath, int keyValue, ChangeHistoryItem change) {
    Integer changeId = queryFactory.insert(changeHistory).populate(change)
        .set(keyPath, keyValue).executeWithKey(changeHistory.id);
    if (changeId == null) {
      throw new QueryException("Failed to insert change");
    }
    List<FieldChange> fields = change.getFieldChanges();
    if (fields != null) {
      for (FieldChange field : fields) {
        Integer fieldId = queryFactory.insert(fieldChange).populate(field).set(fieldChange.changeHistoryId, changeId)
            .executeWithKey(fieldChange.id);
        if (fieldId == null) {
          throw new QueryException("Failed to insert change field");
        }
      }
    }
  }

}
