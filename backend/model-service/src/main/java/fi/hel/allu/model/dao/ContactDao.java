package fi.hel.allu.model.dao;

import com.querydsl.core.QueryResults;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.querydsl.core.types.Projections.bean;
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
    List<Expression<?>> mappedExpressions = new ArrayList<>(Arrays.asList(contact.all()));
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
  public List<Contact> findByCustomer(int customerId) {
    List<Tuple> contactPostalAddress = queryFactory
        .select(contactBean, postalAddressBean)
        .from(contact)
        .leftJoin(postalAddress).on(contact.postalAddressId.eq(postalAddress.id))
        .where(contact.customerId.eq(customerId)).fetch();
    List<Contact> contacts = contactPostalAddress.stream()
            .map(cpa -> PostalAddressUtil.mapPostalAddress(cpa).get(0, Contact.class))
            .collect(Collectors.toList());
    return contacts;
  }

  /**
   * Find all contacts, with paging
   *
   * @param pageRequest page request
   * @return a page of contacts
   */
  @Transactional(readOnly = true)
  public Page<Contact> findAll(Pageable pageRequest) {
    int offset = (pageRequest == null) ? 0 : pageRequest.getOffset();
    int count = (pageRequest == null) ? 100 : pageRequest.getPageSize();
    List<Expression<?>> mappedExpressions = new ArrayList<>(Arrays.asList(contact.all()));
    mappedExpressions.add(bean(PostalAddress.class, postalAddress.all()).as("postalAddress"));
    QueryResults<Contact> queryResults = queryFactory
        .select(Projections.bean(Contact.class, mappedExpressions.toArray(new Expression[0])))
        .from(contact)
        .leftJoin(postalAddress).on(contact.postalAddressId.eq(postalAddress.id))
        .orderBy(contact.id.asc()).offset(offset).limit(count)
        .fetchResults();
    return new PageImpl<>(queryResults.getResults(), pageRequest, queryResults.getTotal());
  }

  @Transactional
  public List<Contact> insert(List<Contact> contacts) {
    contacts.forEach(c -> c.setId(null));
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
