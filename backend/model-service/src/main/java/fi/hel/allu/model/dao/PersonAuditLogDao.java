package fi.hel.allu.model.dao;

import static com.querydsl.core.types.Projections.bean;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import static fi.hel.allu.QPersonAuditLog.personAuditLog;
import fi.hel.allu.model.domain.PersonAuditLogLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class PersonAuditLogDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<PersonAuditLogLog> personAuditLogBean = bean(PersonAuditLogLog.class, personAuditLog.all());

  @Transactional(readOnly = true)
  public Optional<PersonAuditLogLog> findById(int logEntryId) {
    PersonAuditLogLog config = queryFactory.select(personAuditLogBean).from(personAuditLog)
        .where(personAuditLog.id.eq(logEntryId)).fetchOne();
    return Optional.ofNullable(config);
  }

  @Transactional
  public PersonAuditLogLog insert(PersonAuditLogLog logEntry) {
    int id = queryFactory.insert(personAuditLog).populate(logEntry).executeWithKey(personAuditLog.id);
    return findById(id).get();
  }
}
