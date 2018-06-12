package fi.hel.allu.model.dao;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.Projections.bean;
import static com.querydsl.sql.SQLExpressions.select;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QChangeHistory.changeHistory;
import static fi.hel.allu.QFieldChange.fieldChange;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryException;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.QApplication;
import fi.hel.allu.common.types.ChangeType;
import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.ChangeHistoryItemInfo;
import fi.hel.allu.model.domain.FieldChange;

/**
 * The DAO class for handling application history
 */
@Repository
public class HistoryDao {
  @Autowired
  private SQLQueryFactory queryFactory;

  private final QBean<FieldChange> fieldChangeBean = bean(FieldChange.class,
      fieldChange.all());
  private final QBean<ChangeHistoryItem> changeHistoryBean = bean(ChangeHistoryItem.class, changeHistory.all());


  /**
   * Get application's change history
   *
   * @param applicationId
   *          application's database ID
   * @return list of changes, ordered from oldest to newest
   */
  @Transactional(readOnly = true)
  public List<ChangeHistoryItem> getApplicationHistory(int applicationId) {
    List<Integer> applicationIds = new ArrayList<>();
    // Get history also from replaced applications
    getReplacedApplicationIds(applicationId, applicationIds);
    applicationIds.add(applicationId);
    return getChangeHistory(changeHistory.applicationId.in(applicationIds));
  }

  // Gets recursively all application ids replaced by application with given application ID
  private void getReplacedApplicationIds(int applicationId, List<Integer> applicationIds) {
    Integer replacedId = getReplacedApplicationId(applicationId);
    if (replacedId != null) {
      applicationIds.add(replacedId);
      getReplacedApplicationIds(replacedId, applicationIds);
    }
  }

  private Integer getReplacedApplicationId(int applicationId) {
    return queryFactory
        .select(application.replacesApplicationId)
        .from(application)
        .where(application.id.eq(applicationId)).fetchOne();

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

  @Transactional(readOnly = true)
  public List<ChangeHistoryItem> getProjectHistory(int projectId) {
    // Include application status in project history
    final List<Tuple> results = queryFactory.query()
      .union(
        select(changeHistory.all()).from(changeHistory).where(changeHistory.projectId.eq(projectId)),
        select(changeHistory.all()).from(changeHistory).innerJoin(application)
          .on(changeHistory.applicationId.eq(application.id))
          .where(application.projectId.eq(projectId)
            .and(changeHistory.changeType.eq(ChangeType.STATUS_CHANGED))))
      .orderBy(changeHistory.id.desc()).fetch();
    return resultToChangeHistory(results);
  }

  /*
   * Get the change history items that match the given condition.
   */
  private List<ChangeHistoryItem> getChangeHistory(Predicate condition) {
    List<Tuple> results = queryFactory.select(changeHistory.all()).from(changeHistory)
        .where(condition).orderBy(changeHistory.id.asc()).fetch();
    return resultToChangeHistory(results);
  }

  private List<ChangeHistoryItem> resultToChangeHistory(List<Tuple> results) {
    return results.stream()
        .map(r -> new ChangeHistoryItem(r.get(changeHistory.userId), getInfo(r),
            r.get(changeHistory.changeType), r.get(changeHistory.changeSpecifier), r.get(changeHistory.changeTime),
            getChangeLines(r.get(changeHistory.id))))
        .collect(Collectors.toList());
  }

  /*
   * Get all field changes for the given change history item.
   */
  private List<FieldChange> getChangeLines(Integer changeId) {
    return queryFactory.select(fieldChangeBean).from(fieldChange).where(fieldChange.changeHistoryId.eq(changeId))
        .fetch();
  }

  private ChangeHistoryItemInfo getInfo(Tuple result) {
    if (result.get(changeHistory.applicationId) != null) {
      return new ChangeHistoryItemInfo(result.get(changeHistory.applicationId));
    } else {
      return new ChangeHistoryItemInfo(result.get(changeHistory.projectId));
    }
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

  @Transactional
  public void addProjectChange(int projectId, ChangeHistoryItem change) {
    addChangeWithKey(changeHistory.projectId, projectId, change);
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

  @Transactional(readOnly = true)
  public Map<Integer, List<ChangeHistoryItem>> getApplicationStatusChangesForExternalOwner(Integer externalOwnerId, ZonedDateTime eventsAfter,
      List<Integer> includedApplicationIds) {
    QApplication application = QApplication.application;
    BooleanBuilder builder = new BooleanBuilder();
    builder.and(application.externalOwnerId.eq(externalOwnerId));
    builder.and(changeHistory.changeSpecifier.isNotNull());
    if (eventsAfter != null) {
      builder.and(changeHistory.changeTime.after(eventsAfter));
    }
    if (!includedApplicationIds.isEmpty()) {
      builder.and(application.id.in(includedApplicationIds));
    }
    Map<Integer, List<ChangeHistoryItem>> result = queryFactory.select(changeHistory.all())
        .from(changeHistory)
        .join(application).on(application.id.eq(changeHistory.applicationId))
        .where(builder)
        .transform(groupBy(changeHistory.applicationId).as(list(changeHistoryBean)));
    return result;


  }

}
