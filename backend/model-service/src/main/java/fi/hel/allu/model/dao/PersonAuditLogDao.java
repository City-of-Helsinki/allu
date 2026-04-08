package fi.hel.allu.model.dao;

import static com.querydsl.core.types.Projections.bean;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import static fi.hel.allu.QContact.contact;
import static fi.hel.allu.QPersonAuditLog.personAuditLog;
import fi.hel.allu.model.domain.PersonAuditLogLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
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

  /**
   * Permanently deletes person_audit_log rows for the given customer IDs and their associated contacts.
   * Removes entries where customer_id is in the given set, and also entries where contact_id
   * belongs to a contact of any of those customers.
   *
   * @param customerIds customer IDs whose audit log entries should be deleted
   */
  @Transactional
  public void deleteByCustomerIds(Collection<Integer> customerIds) {
    if (customerIds == null || customerIds.isEmpty()) {
      return;
    }
    // Fetch contact IDs belonging to these customers
    List<Integer> contactIds = queryFactory
      .select(contact.id)
      .from(contact)
      .where(contact.customerId.in(customerIds))
      .fetch();

    // Delete rows linked to the customers directly
    queryFactory
      .delete(personAuditLog)
      .where(personAuditLog.customerId.in(customerIds))
      .execute();

    // Delete rows linked to the customers' contacts
    if (!contactIds.isEmpty()) {
      queryFactory
        .delete(personAuditLog)
        .where(personAuditLog.contactId.in(contactIds))
        .execute();
    }
  }
}
