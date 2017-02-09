package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.model.domain.ApplicationChange;
import fi.hel.allu.model.domain.ApplicationFieldChange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplicationChange.applicationChange;
import static fi.hel.allu.QApplicationFieldChange.applicationFieldChange;

/**
 * The DAO class for handling application history
 */
@Repository
public class HistoryDao {
  @Autowired
  private SQLQueryFactory queryFactory;

  private final QBean<ApplicationFieldChange> fieldChangeBean = bean(ApplicationFieldChange.class,
      applicationFieldChange.all());
  /**
   * Get application's change history
   *
   * @param applicationId
   *          application's database ID
   * @return list of changes, ordered from oldest to newest
   */
  @Transactional(readOnly = true)
  public List<ApplicationChange> getApplicationHistory(int applicationId) {
    List<Tuple> results = queryFactory.select(applicationChange.all()).from(applicationChange)
        .where(applicationChange.applicationId.eq(applicationId)).orderBy(applicationChange.id.asc()).fetch();
    return results.stream()
        .map(r -> new ApplicationChange(r.get(applicationChange.userId), r.get(applicationChange.changeType),
            r.get(applicationChange.newStatus),
            r.get(applicationChange.changeTime), getChangeLines(r.get(applicationChange.id))))
        .collect(Collectors.toList());
  }

  private List<ApplicationFieldChange> getChangeLines(Integer changeId) {
    return queryFactory.select(fieldChangeBean).from(applicationFieldChange)
        .where(applicationFieldChange.applicationChangeId.eq(changeId)).fetch();
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
  public void addApplicationChange(int applicationId, ApplicationChange change) {
    Integer changeId = queryFactory.insert(applicationChange).populate(change)
        .set(applicationChange.applicationId, applicationId).executeWithKey(applicationChange.id);
    if (changeId == null) {
      throw new QueryException("Failed to insert change");
    }
    List<ApplicationFieldChange> fields = change.getFieldChanges();
    if (fields != null) {
      for (ApplicationFieldChange field : fields) {
        Integer fieldId = queryFactory.insert(applicationFieldChange).populate(field)
            .set(applicationFieldChange.applicationChangeId, changeId).executeWithKey(applicationFieldChange.id);
        if (fieldId == null) {
          throw new QueryException("Failed to insert change field");
        }
      }
    }
  }
}
