package fi.hel.allu.model.controller;

import fi.hel.allu.model.domain.StoredFilter;
import fi.hel.allu.model.service.StoredFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class StoredFilterController {

  @Autowired
  private StoredFilterService service;

  @GetMapping(value = "/stored-filter/{id}")
  public ResponseEntity<StoredFilter> findById(@PathVariable int id) {
    return new ResponseEntity<>(service.findById(id), HttpStatus.OK);
  }

  @GetMapping(value = "/user/{userId}/stored-filter")
  public ResponseEntity<List<StoredFilter>> findByUser(@PathVariable int userId) {
    return new ResponseEntity<>(service.findByUser(userId), HttpStatus.OK);
  }

  @PostMapping(value = "/stored-filter")
  public ResponseEntity<StoredFilter> insert(@Valid @RequestBody StoredFilter filter) {
    return new ResponseEntity<>(service.insert(filter), HttpStatus.OK);
  }

  @PutMapping(value = "/stored-filter/{id}")
  public ResponseEntity<StoredFilter> update(@PathVariable int id, @Valid @RequestBody StoredFilter filter) {
    filter.setId(id);
    return new ResponseEntity<>(service.update(filter), HttpStatus.OK);
  }

  @DeleteMapping(value = "/stored-filter/{id}")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    service.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping(value = "/stored-filter/{id}/set-default")
  public ResponseEntity<Void> setAsDefault(@PathVariable int id) {
    service.setAsDefault(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}