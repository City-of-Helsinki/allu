package fi.hel.allu.ui.controller;

import fi.hel.allu.servicecore.domain.StoredFilterJson;
import fi.hel.allu.servicecore.service.StoredFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class StoredFilterController {
  @Autowired
  private StoredFilterService service;

  @GetMapping(value = "/stored-filter/{id}")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<StoredFilterJson> findById(@PathVariable int id) {
    return new ResponseEntity<>(service.findById(id), HttpStatus.OK);
  }

  @GetMapping(value = "/users/{userId}/stored-filter")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<StoredFilterJson>> findByUser(@PathVariable int userId) {
    return new ResponseEntity<>(service.findByUser(userId), HttpStatus.OK);
  }

  @PostMapping(value = "/stored-filter")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<StoredFilterJson> insert(@Valid @RequestBody StoredFilterJson filter) {
    return new ResponseEntity<>(service.insert(filter), HttpStatus.OK);
  }

  @PutMapping(value = "/stored-filter/{id}")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<StoredFilterJson> update(@PathVariable int id, @Valid @RequestBody StoredFilterJson filter) {
    filter.setId(id);
    return new ResponseEntity<>(service.update(filter), HttpStatus.OK);
  }

  @DeleteMapping(value = "/stored-filter/{id}")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    service.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping(value = "/stored-filter/{id}/set-default")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<Void> setAsDefault(@PathVariable int id) {
    service.setAsDefault(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}