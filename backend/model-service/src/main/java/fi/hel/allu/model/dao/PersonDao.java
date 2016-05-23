package fi.hel.allu.model.dao;

import fi.hel.allu.model.domain.Person;

public interface PersonDao {
    public Person findById(int id);

    public Person insert(Person person);

    public Person update(int id, Person person);

    public void deleteAll();
}
