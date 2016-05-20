package fi.hel.allu.model.dao;

import java.util.List;

import javax.inject.Inject;

import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.model.domain.Person;
import fi.vincit.allu.QPerson;

public class PersonDaoImpl implements PersonDao {

    @Inject
    private SQLQueryFactory queryFactory;

    @Override
    public Person findById(int id) {
        QPerson person = new QPerson("p");
        List<Person> persons = queryFactory.select(Projections.bean(Person.class, person.all())).from(person)
                .where(person.id.eq(id)).fetch();
        return persons.isEmpty() ? null : persons.get(0);
    }

    @Override
    public Person insert(Person personData) {
        QPerson person = new QPerson("p");
        int id = queryFactory.insert(person).populate(personData).executeWithKey(person.id);
        return findById(id);
    }

    @Override
    public Person update(int id, Person personData) {
        QPerson person = new QPerson("p");
        personData.setId(id);
        queryFactory.update(person).where(person.id.eq(id)).populate(personData).execute();
        return findById(id);
    }

    @Override
    public void deleteAll() {
        QPerson person = new QPerson("p");
        queryFactory.delete(person).execute();
    }

}
