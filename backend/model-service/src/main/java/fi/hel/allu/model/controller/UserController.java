package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.UserDao;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.common.domain.UserSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Controller for managing Allu users.
 */
@RestController
@RequestMapping("/users")
public class UserController {

  private UserDao userDao;

  @Autowired
  public UserController(UserDao userDao) {
    this.userDao = userDao;
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<User>> getUsers() {
    return new ResponseEntity<>(userDao.findAll(), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<User> findById(@PathVariable int id) {
    User user = userDao.findById(id).orElseThrow(() -> new NoSuchEntityException("user.notFound", Integer.toString(id)));
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<List<User>> search(@Valid @RequestBody UserSearchCriteria usc) {
    return new ResponseEntity<>(
        userDao.findMatching(usc.getRoleType(), usc.getApplicationType(), usc.getCityDistrictId()),
        HttpStatus.OK);
  }

  @RequestMapping(value = "/role/{roleType}", method = RequestMethod.GET)
  public ResponseEntity<List<User>> findByRole(@PathVariable RoleType roleType) {
    return new ResponseEntity<>(userDao.findByRole(roleType), HttpStatus.OK);
  }

  @RequestMapping(value = "/userName/{userName}", method = RequestMethod.GET)
  public ResponseEntity<User> getUser(@PathVariable String userName) {
    User user = userDao.findByUserName(userName).orElseThrow(() -> new NoSuchEntityException("user.notFound", userName));
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<User> addUser(@RequestBody User user) {
    return new ResponseEntity<>(userDao.insert(user), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.PUT)
  public ResponseEntity updateUser(@RequestBody User user) throws NoSuchEntityException {
    userDao.update(user);
    return new ResponseEntity(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/lastLogin", method = RequestMethod.PUT)
  public void setLastLogin(@PathVariable int id, @RequestBody ZonedDateTime loginTime) {
    userDao.setLastLogin(id, loginTime);
  }
}
