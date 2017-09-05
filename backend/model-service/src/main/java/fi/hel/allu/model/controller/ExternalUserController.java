package fi.hel.allu.model.controller;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.ExternalUserDao;
import fi.hel.allu.model.domain.ExternalUser;
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

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<ExternalUser>> getUsers() {
    return new ResponseEntity<>(externalUserDao.findAll(), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<ExternalUser> findById(@PathVariable int id) {
    ExternalUser user = externalUserDao.findById(id).orElseThrow(() -> new NoSuchEntityException("No such external user", Integer.toString(id)));
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @RequestMapping(value = "/username/{username}", method = RequestMethod.GET)
  public ResponseEntity<ExternalUser> getUser(@PathVariable String username) {
    ExternalUser user = externalUserDao.findByUsername(username).orElseThrow(() -> new NoSuchEntityException("No such external user", username));
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<ExternalUser> addUser(@RequestBody ExternalUser user) {
    return new ResponseEntity<>(externalUserDao.insert(user), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.PUT)
  public ResponseEntity updateUser(@RequestBody ExternalUser user) throws NoSuchEntityException {
    externalUserDao.update(user);
    return new ResponseEntity(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/lastLogin", method = RequestMethod.PUT)
  public void setLastLogin(@PathVariable int id, @RequestBody ZonedDateTime loginTime) {
    externalUserDao.setLastLogin(id, loginTime);
  }
}
