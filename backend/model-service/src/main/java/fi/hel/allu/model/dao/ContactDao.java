package fi.hel.allu.model.dao;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QContact.contact;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Contact;

public class ContactDao {
  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<Contact> contactBean = bean(Contact.class, contact.all());

  @Transactional(readOnly = true)
  public Optional<Contact> findById(int id) {
    Contact cont = queryFactory.select(contactBean).from(contact).where(contact.id.eq(id)).fetchOne();
    return Optional.ofNullable(cont);
  }

  @Transactional(readOnly = true)
  public List<Contact> findByOrganization(int organizationId) {
    return queryFactory.select(contactBean).from(contact).where(contact.organizationId.eq(organizationId)).fetch();
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
    long changed = queryFactory.update(contact).populate(contactData).where(contact.id.eq(id)).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update the record", Integer.toString(id));
    }
    return findById(id).get();
  }
}
