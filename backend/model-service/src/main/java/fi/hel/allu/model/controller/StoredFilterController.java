package fi.hel.allu.model.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import fi.hel.allu.common.domain.types.StoredFilterType;
import fi.hel.allu.model.domain.StoredFilter;
import fi.hel.allu.model.service.StoredFilterService;

@RestController
public class StoredFilterController {

  @Autowired
  private StoredFilterService service;

  @RequestMapping(value = "/stored-filter/{id}", method = RequestMethod.GET)
  public ResponseEntity<StoredFilter> findById(@PathVariable int id) {
    return new ResponseEntity<>(service.findById(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/user/{userId}/stored-filter/{type}", method = RequestMethod.GET)
  public ResponseEntity<List<StoredFilter>> findByUserAndType(
    @PathVariable int userId, @PathVariable StoredFilterType type) {
    return new ResponseEntity<>(service.findByUserAndType(userId, type), HttpStatus.OK);
  }

  @RequestMapping(value = "/stored-filter", method = RequestMethod.POST)
  public ResponseEntity<StoredFilter> insert(@Valid @RequestBody StoredFilter filter) {
    return new ResponseEntity<>(service.insert(filter), HttpStatus.OK);
  }

  @RequestMapping(value = "/stored-filter/{id}", method = RequestMethod.PUT)
  public ResponseEntity<StoredFilter> update(@PathVariable int id, @Valid @RequestBody StoredFilter filter) {
    filter.setId(id);
    return new ResponseEntity<>(service.update(filter), HttpStatus.OK);
  }

  @RequestMapping(value = "/stored-filter/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable int id) {
    service.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/stored-filter/{id}/set-default", method = RequestMethod.PUT)
  public ResponseEntity<Void> setAsDefault(@PathVariable int id) {
    service.setAsDefault(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
