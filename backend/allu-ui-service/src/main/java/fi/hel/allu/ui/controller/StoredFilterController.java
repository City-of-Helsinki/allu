package fi.hel.allu.ui.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import fi.hel.allu.servicecore.domain.StoredFilterJson;
import fi.hel.allu.servicecore.service.StoredFilterService;

@RestController
public class StoredFilterController {
  @Autowired
  private StoredFilterService service;

  @RequestMapping(value = "/stored-filter/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<StoredFilterJson> findById(@PathVariable int id) {
    return new ResponseEntity<>(service.findById(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/users/{userId}/stored-filter", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<StoredFilterJson>> findByUser(@PathVariable int userId) {
    return new ResponseEntity<>(service.findByUser(userId), HttpStatus.OK);
  }

  @RequestMapping(value = "/stored-filter", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<StoredFilterJson> insert(@Valid @RequestBody StoredFilterJson filter) {
    return new ResponseEntity<>(service.insert(filter), HttpStatus.OK);
  }

  @RequestMapping(value = "/stored-filter/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<StoredFilterJson> update(@PathVariable int id, @Valid @RequestBody StoredFilterJson filter) {
    filter.setId(id);
    return new ResponseEntity<>(service.update(filter), HttpStatus.OK);
  }

  @RequestMapping(value = "/stored-filter/{id}", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    service.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/stored-filter/{id}/set-default", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<Void> setAsDefault(@PathVariable int id) {
    service.setAsDefault(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
