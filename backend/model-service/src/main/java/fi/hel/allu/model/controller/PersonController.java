package fi.hel.allu.model.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.PersonDao;
import fi.hel.allu.model.domain.Person;

@RestController
@RequestMapping("/persons")
public class PersonController {

  @Autowired
  private PersonDao personDao;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Person> person(@PathVariable int id) {
    Optional<Person> person = personDao.findById(id);
    Person personValue = person.orElseThrow(() -> new NoSuchEntityException("Person not found", Integer.toString(id)));
    return new ResponseEntity<>(personValue, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Person> updatePerson(@PathVariable int id, @Valid @RequestBody(required = true) Person person) {
    Person resultPerson = personDao.update(id, person);
    return new ResponseEntity<>(resultPerson, resultPerson != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Person> addPerson(@Valid @RequestBody(required = true) Person person) {
    if (person.getId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    return new ResponseEntity<>(personDao.insert(person), HttpStatus.OK);
  }

}
