package fi.hel.allu.model.dao;

import java.util.*;
import java.util.stream.Collectors;

import com.querydsl.sql.dml.SQLInsertClause;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.EmptyUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
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
import fi.hel.allu.model.domain.changehistory.HistorySearchCriteria;

import static com.querydsl.core.types.Projections.bean;
import static com.querydsl.sql.SQLExpressions.select;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QChangeHistory.changeHistory;
import static fi.hel.allu.QFieldChange.fieldChange;

/**
 * The DAO class for handling application history
 */
@Repository
public class HistoryDao {

  private final SQLQueryFactory queryFactory;
  private final QBean<ChangeHistoryItem> changeHistoryBean = bean(ChangeHistoryItem.class, changeHistory.all());

  public HistoryDao(SQLQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  /**
   * Get application's change history
   *
   * @param applicationId
   *          application's database ID
   * @return list of changes, ordered from oldest to newest
   */
  @Transactional(readOnly = true)
  public List<ChangeHistoryItem> getApplicationHistory(int applicationId) {
    return getApplicationHistory(getWithReplacedApplicationIds(applicationId));
  }

  /**
   * Get change history for given application id's
   *
   * @param applicationIds
   *          list of application database IDs
   * @return list of changes, ordered from oldest to newest
   */
  @Transactional(readOnly = true)
  public List<ChangeHistoryItem> getApplicationHistory(List<Integer> applicationIds) {
    Path[] fields = ArrayUtils.addAll(changeHistory.all(), application.applicationId, application.name);
    fields = ArrayUtils.addAll(fields, fieldChange.all());
    List<Tuple> results = queryFactory.select(fields)
      .from(changeHistory)
      .leftJoin(application).on(changeHistory.applicationId.eq(application.id))
      .leftJoin(fieldChange).on(fieldChange.changeHistoryId.eq(changeHistory.id))
      .where(changeHistory.applicationId.in(applicationIds))
      .orderBy(changeHistory.id.desc()).fetch();
    return resultToChangeHistory(results);
  }

  // Gets recursively all application ids replaced by application with given application ID
  @Transactional(readOnly = true)
  public void getReplacedApplicationIds(int applicationId, List<Integer> applicationIds) {
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

  private List<Integer> getWithReplacedApplicationIds(int applicationId) {
    List<Integer> applicationIds = new ArrayList<>();
    // Get history also from replaced applications
    getReplacedApplicationIds(applicationId, applicationIds);
    applicationIds.add(applicationId);
    return applicationIds;
  }

  /**
   * Get customer's change history
   *
   * @param customerId customer's database ID
   * @return list of changes, ordered from oldest to newest
   */
  @Transactional(readOnly = true)
  public List<ChangeHistoryItem> getCustomerHistory(int customerId) {
    return getChangeHistory(changeHistory.customerId.eq(customerId));
  }


  /**
   * Get project change history
   *
   * @param projectId project database ID
   * @return list of changes, ordered from oldest to newest
   */
  @Transactional(readOnly = true)
  public List<ChangeHistoryItem> getProjectHistory(int projectId) {
    // Include application status in project history
    Path[] fields = ArrayUtils.addAll(changeHistory.all(), fieldChange.all());
    final List<Tuple> results = queryFactory.query()
      .union(
        select(fields).from(changeHistory)
          .leftJoin(fieldChange).on(fieldChange.changeHistoryId.eq(changeHistory.id))
          .where(changeHistory.projectId.eq(projectId)),
        select(fields).from(changeHistory)
          .innerJoin(application).on(changeHistory.applicationId.eq(application.id))
          .leftJoin(fieldChange).on(fieldChange.changeHistoryId.eq(changeHistory.id))
          .where(application.projectId.eq(projectId)
            .and(changeHistory.changeType.eq(ChangeType.STATUS_CHANGED))))
      .orderBy(changeHistory.id.desc()).fetch();
    return resultToChangeHistory(results);
  }

  /*
   * Get the change history items that match the given condition.
   */
  private List<ChangeHistoryItem> getChangeHistory(Predicate condition) {
    Path[] fields = ArrayUtils.addAll(changeHistory.all(), fieldChange.all());
    List<Tuple> results = queryFactory.select(fields)
      .from(changeHistory)
      .leftJoin(fieldChange).on(fieldChange.changeHistoryId.eq(changeHistory.id))
      .where(condition).orderBy(changeHistory.id.desc()).fetch();
    return resultToChangeHistory(results);
  }

  private List<ChangeHistoryItem> resultToChangeHistory(List<Tuple> results) {
    return results.stream()
      .map(result -> results.stream()
        .filter(rf -> Objects.equals(rf.get(changeHistory.id), result.get(changeHistory.id)))
        .collect(Collectors.toList()))
      .distinct()
      .map(changeHistoryWithFieldChangeList -> {
        Tuple rChangeHistory = changeHistoryWithFieldChangeList.get(0);
        return new ChangeHistoryItem(rChangeHistory.get(changeHistory.userId), getInfo(rChangeHistory),
          rChangeHistory.get(changeHistory.changeType), rChangeHistory.get(changeHistory.changeSpecifier),
          rChangeHistory.get(changeHistory.changeTime), getChangeLines(changeHistoryWithFieldChangeList),
          rChangeHistory.get(changeHistory.changeSpecifier2));
      })
      .collect(Collectors.toList());
  }

  /*
   * Get all field changes for the given change history item.
   */
  private List<FieldChange> getChangeLines(List<Tuple> fieldChanges) {
    return fieldChanges.stream()
      .filter(f -> StringUtils.isNotEmpty(f.get(fieldChange.fieldName)))
      .map(f ->
      new FieldChange(f.get(fieldChange.fieldName),
        f.get(fieldChange.oldValue), f.get(fieldChange.newValue))
    )
      .collect(Collectors.toList());
  }

  private ChangeHistoryItemInfo getInfo(Tuple result) {
    if (result.get(changeHistory.applicationId) != null) {
      ChangeHistoryItemInfo info = new ChangeHistoryItemInfo(result.get(changeHistory.applicationId));
      info.setApplicationId(result.get(application.applicationId));
      info.setName(result.get(application.name));
      return info;
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
    if (EmptyUtil.isNotEmpty(fields)) {
      SQLInsertClause insertClause = queryFactory.insert(fieldChange);
      for (FieldChange field : fields) {
        insertClause.populate(field).set(fieldChange.changeHistoryId, changeId).addBatch();
      }
      List<Integer> fieldIds = insertClause.executeWithKeys(fieldChange.id);
      if (fieldIds == null || fieldIds.size() != fields.size()) {
        throw new QueryException("Failed to insert change field");
      }
    }
  }

  @Transactional(readOnly = true)
  public Map<Integer, List<ChangeHistoryItem>> getApplicationChangesForExternalOwner(Integer externalOwnerId, HistorySearchCriteria searchCriteria) {
    QApplication application = QApplication.application;

    final QBean<ExternalApplicationId> applicationIdBean = bean(ExternalApplicationId.class, application.applicationId, application.externalApplicationId);

    BooleanBuilder builder = new BooleanBuilder();
    builder.and(application.externalOwnerId.eq(externalOwnerId));
    builder.and(changeHistory.changeSpecifier.isNotNull());
    builder.and(changeHistory.changeType.in(searchCriteria.getChangeTypes()));

    if (searchCriteria.getEventsAfter() != null) {
      builder.and(changeHistory.changeTime.after(searchCriteria.getEventsAfter()));
    }
    if (!searchCriteria.getApplicationIds().isEmpty()) {
      builder.and(application.externalApplicationId.in(searchCriteria.getApplicationIds()));
    }
    List<Tuple> result = queryFactory.select(changeHistoryBean, applicationIdBean)
        .from(changeHistory)
        .join(application).on(application.id.eq(changeHistory.applicationId))
        .where(builder)
        .fetch();
    return result.stream()
        .collect(Collectors.groupingBy(t -> t.get(1, ExternalApplicationId.class).getId(),
                 Collectors.mapping(this::toChangeHistoryWithApplicationId, Collectors.toList())));
   }

  @Transactional(readOnly = true)
  public Boolean applicationHasStatusInHistory(int applicationId, StatusType status) {
    List<Integer> applicationIds = getWithReplacedApplicationIds(applicationId);
    return queryFactory.select(changeHistory.all())
      .from(changeHistory)
      .where(changeHistory.applicationId.in(applicationIds)
        .and(changeHistory.changeType.eq(ChangeType.STATUS_CHANGED))
        .and(changeHistory.changeSpecifier.eq(status.name())))
      .fetchCount() > 0;
  }

  private ChangeHistoryItem toChangeHistoryWithApplicationId(Tuple tuple) {
    String applicationId = tuple.get(1, ExternalApplicationId.class).getApplicationId();
    ChangeHistoryItem item = tuple.get(0, ChangeHistoryItem.class);
    item.setInfo(new ChangeHistoryItemInfo(applicationId));
    return item;
  }

  public static class ExternalApplicationId {
    private Integer id;
    private String applicationId;

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public String getApplicationId() {
      return applicationId;
    }

    public void setApplicationId(String applicationId) {
      this.applicationId = applicationId;
    }
  }
}