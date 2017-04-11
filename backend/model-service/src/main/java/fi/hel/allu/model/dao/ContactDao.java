package fi.hel.allu.model.dao;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.DefaultMapper;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.common.PostalAddressUtil;
import fi.hel.allu.model.domain.Contact;
import fi.hel.allu.model.domain.PostalAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplicationContact.applicationContact;
import static fi.hel.allu.QContact.contact;
import static fi.hel.allu.QPostalAddress.postalAddress;

@Repository
public class ContactDao {

  private static final Logger logger = LoggerFactory.getLogger(ContactDao.class);

  @Autowired
  private SQLQueryFactory queryFactory;
  @Autowired
  PostalAddressDao postalAddressDao;

  final QBean<Contact> contactBean = bean(Contact.class, contact.all());
  final QBean<PostalAddress> postalAddressBean = bean(PostalAddress.class, postalAddress.all());

  @Transactional(readOnly = true)
  public Optional<Contact> findById(int id) {
    Tuple contactPostalAddress = queryFactory
        .select(contactBean, postalAddressBean)
        .from(contact)
        .leftJoin(postalAddress).on(contact.postalAddressId.eq(postalAddress.id))
        .where(contact.id.eq(id)).fetchOne();
    Contact foundContact = null;
    if (contactPostalAddress != null) {
      foundContact = PostalAddressUtil.mapPostalAddress(contactPostalAddress).get(0, Contact.class);
    }
    return Optional.ofNullable(foundContact);
  }

  @Transactional
  public List<Contact> findByIds(List<Integer> ids) {
    List<Expression> mappedExpressions = new ArrayList<>(Arrays.asList(contact.all()));
    mappedExpressions.add(bean(PostalAddress.class, postalAddress.all()).as("postalAddress"));
    List<Contact> contacts = queryFactory
        .select(Projections.bean(Contact.class, mappedExpressions.toArray(new Expression[0])))
        .from(contact)
        .leftJoin(postalAddress).on(contact.postalAddressId.eq(postalAddress.id))
        .where(contact.id.in(ids))
        .fetch();
    return contacts;
  }

  @Transactional(readOnly = true)
  public List<Contact> findByApplicant(int applicantId) {
    List<Tuple> contactPostalAddress = queryFactory
        .select(contactBean, postalAddressBean)
        .from(contact)
        .leftJoin(postalAddress).on(contact.postalAddressId.eq(postalAddress.id))
        .where(contact.applicantId.eq(applicantId)).fetch();
    List<Contact> contacts = contactPostalAddress.stream()
            .map(cpa -> PostalAddressUtil.mapPostalAddress(cpa).get(0, Contact.class))
            .collect(Collectors.toList());
    return contacts;
  }

  @Transactional(readOnly = true)
  public List<Contact> findByApplication(int applicationId) {
    List<Tuple> contactPostalAddress = queryFactory
        .select(contactBean, postalAddressBean)
        .from(contact)
        .innerJoin(applicationContact).on(contact.id.eq(applicationContact.contactId))
        .leftJoin(postalAddress).on(contact.postalAddressId.eq(postalAddress.id))
        .where(applicationContact.applicationId.eq(applicationId))
        .orderBy(applicationContact.position.asc()).fetch();
    List<Contact> contacts = contactPostalAddress.stream()
        .map(cpa -> PostalAddressUtil.mapPostalAddress(cpa).get(0, Contact.class))
        .collect(Collectors.toList());
    return contacts;
  }

  @Transactional
  public List<Contact> setApplicationContacts(int applicationId, List<Contact> contacts) {
    // remove old contact links, then add new ones.
    queryFactory.delete(applicationContact).where(applicationContact.applicationId.eq(applicationId)).execute();
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

  @Transactional
  public List<Contact> insert(List<Contact> contacts) {
    SQLInsertClause inserts = queryFactory.insert(contact);
    contacts.forEach(c -> inserts.populate(c).set(contact.postalAddressId, postalAddressDao.insertIfNotNull(c)).addBatch());
    List<Integer> ids = inserts.executeWithKeys(contact.id);
    return findByIds(ids);
  }

  @Transactional
  public List<Contact> update(List<Contact> contacts) {
    return contacts.stream().map(c -> update(c.getId(), c)).collect(Collectors.toList());
  }

  private Contact update(int id, Contact contactData) {
    contactData.setId(id);
    Contact currentContact = findById(id).orElseThrow(
        () -> new NoSuchEntityException("Attempted to update non-existent contact", Integer.toString(id)));

    Integer deletedPostalAddressId = postalAddressDao.mapAndUpdatePostalAddress(currentContact, contactData);
    Integer postalAddressId = Optional.ofNullable(currentContact.getPostalAddress()).map(pAddress -> pAddress.getId()).orElse(null);

    SQLUpdateClause query = queryFactory
        .update(contact)
        .populate(contactData, DefaultMapper.WITH_NULL_BINDINGS)
        .set(contact.postalAddressId, postalAddressId)
        .where(contact.id.eq(id));
    for (SQLBindings sql : query.getSQL()) {
      logger.debug(sql.getSQL());
    }
    long changed = query.execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update the record", Integer.toString(id));
    }
    if (deletedPostalAddressId != null) {
      postalAddressDao.delete(Collections.singletonList(deletedPostalAddressId));
    }

    return findById(id).get();
  }
}
