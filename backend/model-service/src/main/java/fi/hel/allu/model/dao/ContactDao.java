package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.DefaultMapper;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplicationContact.applicationContact;
import static fi.hel.allu.QContact.contact;

@Repository
public class ContactDao {

  private static final Logger logger = LoggerFactory.getLogger(ContactDao.class);

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<Contact> contactBean = bean(Contact.class, contact.all());

  @Transactional(readOnly = true)
  public Optional<Contact> findById(int id) {
    Contact cont = queryFactory.select(contactBean).from(contact).where(contact.id.eq(id)).fetchOne();
    return Optional.ofNullable(cont);
  }

  @Transactional(readOnly = true)
  public List<Contact> findByApplicant(int applicantId) {
    return queryFactory.select(contactBean).from(contact).where(contact.applicantId.eq(applicantId)).fetch();
  }

  @Transactional(readOnly = true)
  public List<Contact> findByApplication(int applicationId) {
    SQLQuery<Contact> query = queryFactory.select(contactBean).from(contact).innerJoin(applicationContact)
        .on(contact.id.eq(applicationContact.contactId)).where(applicationContact.applicationId.eq(applicationId))
        .orderBy(applicationContact.position.asc());
    logger.debug(String.format("Executing query \"%s\"", query.getSQL().getSQL()));
    return query.fetch();
  }

  @Transactional
  public List<Contact> setApplicationContacts(int applicationId, List<Contact> contacts) {
    // remove old contact links, then add new ones.
    queryFactory.delete(applicationContact).where(applicationContact.applicationId.eq(applicationId)).execute();
    // Update/insert the contacts first...
    contacts = contacts.stream().map(c -> storeContact(c)).collect(Collectors.toList());
    // ... then create the link records
    SQLInsertClause insertClause = queryFactory.insert(applicationContact);
    int pos = 0;
    for (Contact contact : contacts) {
      insertClause.columns(applicationContact.applicationId, applicationContact.contactId, applicationContact.position)
          .values(applicationId, contact.getId(), pos++).addBatch();
    }
    insertClause.execute();
    return findByApplication(applicationId);
  }

  private Contact storeContact(Contact contactItem) {
    if (contactItem.getId() != null) {
      return update(contactItem.getId(), contactItem);
    } else {
      return insert(contactItem);
    }
  }

  @Transactional
  public Contact insert(Contact contactData) {
    Integer id = queryFactory.insert(contact).populate(contactData).executeWithKey(contact.id);
    if (id == null) {
      throw new QueryException("Failed to insert record");
    }
    return findById(id).get();
  }

  @Transactional
  public Contact update(int id, Contact contactData) {
    contactData.setId(id);
    SQLUpdateClause query = queryFactory.update(contact).populate(contactData, DefaultMapper.WITH_NULL_BINDINGS)
        .where(contact.id.eq(id));
    for (SQLBindings sql : query.getSQL()) {
      logger.debug(sql.getSQL());
    }
    long changed = query.execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update the record", Integer.toString(id));
    }
    return findById(id).get();
  }

}
