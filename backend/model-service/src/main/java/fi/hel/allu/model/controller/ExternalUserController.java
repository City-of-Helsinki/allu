package fi.hel.allu.model.controller;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.ExternalUserDao;
import fi.hel.allu.model.domain.user.ExternalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Controller for managing Allu users.
 */
@RestController
@RequestMapping("/externalusers")
public class ExternalUserController {

  private ExternalUserDao externalUserDao;

  @Autowired
  public ExternalUserController(ExternalUserDao userDao) {
    this.externalUserDao = userDao;
  }

  @GetMapping
  public ResponseEntity<List<ExternalUser>> getUsers() {
    return new ResponseEntity<>(externalUserDao.findAll(), HttpStatus.OK);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<ExternalUser> findById(@PathVariable int id) {
    ExternalUser user = externalUserDao.findById(id).orElseThrow(() -> new NoSuchEntityException("No such external user", Integer.toString(id)));
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @GetMapping(value = "/username/{username}")
  public ResponseEntity<ExternalUser> getUser(@PathVariable String username) {
    ExternalUser user = externalUserDao.findByUsername(username).orElseThrow(() -> new NoSuchEntityException("No such external user", username));
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<ExternalUser> addUser(@RequestBody ExternalUser user) {
    return new ResponseEntity<>(externalUserDao.insert(user), HttpStatus.OK);
  }

  @PutMapping
  public ResponseEntity<Void> updateUser(@RequestBody ExternalUser user) throws NoSuchEntityException {
    externalUserDao.update(user);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/password")
  public ResponseEntity<Void> setPassword(@PathVariable Integer id, @RequestBody String password) throws NoSuchEntityException {
    externalUserDao.setPassword(id, password);
    return new ResponseEntity<>(HttpStatus.OK);
  }


  @PutMapping(value = "/{id}/lastLogin")
  public void setLastLogin(@PathVariable int id, @RequestBody ZonedDateTime loginTime) {
    externalUserDao.setLastLogin(id, loginTime);
  }
}