package fi.hel.allu.model.dao;

import static com.querydsl.core.types.Projections.bean;
import static fi.vincit.allu.QPerson.person;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Person;

public class PersonDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<Person> personBean = bean(Person.class, person.all());

  @Transactional(readOnly = true)
  public Optional<Person> findById(int id) {
    Person pers = queryFactory.select(personBean).from(person).where(person.id.eq(id)).fetchOne();
    return Optional.ofNullable(pers);
  }

  @Transactional
  public Person insert(Person personData) {
    Integer id = queryFactory.insert(person).populate(personData).executeWithKey(person.id);
    if (id == null) {
      throw new QueryException("Failed to insert record");
    }
    return findById(id).get();
  }

  @Transactional
  public Person update(int id, Person personData) {
    personData.setId(id);
    long changed = queryFactory.update(person).populate(personData).where(person.id.eq(id)).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update the record", Integer.toString(id));
    }
    return findById(id).get();
  }

}
