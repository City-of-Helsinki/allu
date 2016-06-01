package fi.hel.allu.model.controller;

import fi.hel.allu.model.dao.PersonDao;
import fi.hel.allu.model.domain.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/persons")
public class PersonController {

    @Autowired
    private PersonDao personDao;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Person> person(@PathVariable int id) {
        Optional<Person> person = personDao.findById(id);
        if (person.isPresent()) {
            return new ResponseEntity<>(person.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Person> updatePerson(@PathVariable int id, @RequestBody(required = true) Person person) {
        Person resultPerson = personDao.update(id, person);
        return new ResponseEntity<>(resultPerson, resultPerson != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Person> addPerson(@RequestBody(required = true) Person person) {
        if (person.getId() != null) {
            throw new IllegalArgumentException("Id must be null for insert");
        }
        return new ResponseEntity<>(personDao.insert(person), HttpStatus.OK);
    }

}
