package fi.hel.allu.model.controller;

import fi.hel.allu.model.dao.CustomerDao;
import fi.hel.allu.model.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    
    @Autowired
    private CustomerDao customerDao;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Customer> find(@PathVariable int id) {
        Optional<Customer> customer = customerDao.findById(id);
        if (customer.isPresent()) {
            return new ResponseEntity<>(customer.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Customer> update(@PathVariable int id, @RequestBody(required = true) Customer customer) {
        return new ResponseEntity<>(customerDao.update(id, customer), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Customer> insert(@RequestBody(required = true) Customer customer) {
        if (customer.getId() != null) {
            throw new IllegalArgumentException("Id must be null for insert");
        }
        return new ResponseEntity<>(customerDao.insert(customer), HttpStatus.OK);
    }
}

