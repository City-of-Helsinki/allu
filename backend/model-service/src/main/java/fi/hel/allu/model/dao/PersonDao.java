package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.NoSuchEntityException;
import fi.hel.allu.model.domain.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.querydsl.core.types.Projections.bean;
import static fi.vincit.allu.QPerson.person;

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
        if (changed != 1) {
            throw new QueryException("Failed to update the record");
        }
        return findById(id).get();
    }

}
