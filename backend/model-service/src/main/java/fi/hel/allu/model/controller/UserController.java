package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.UserSearchCriteria;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.UserDao;
import fi.hel.allu.model.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing Allu users.
 */
@RestController
@RequestMapping("/users")
public class UserController {

  private final UserDao userDao;

  @Autowired
  public UserController(UserDao userDao) {
    this.userDao = userDao;
  }

  @GetMapping
  public ResponseEntity<List<User>> getUsers() {
    return new ResponseEntity<>(userDao.findAll(), HttpStatus.OK);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<User> findById(@PathVariable int id) {
    User user = userDao.findById(id).orElseThrow(() -> new NoSuchEntityException("user.notFound", Integer.toString(id)));
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @PostMapping(value = "/search")
  public ResponseEntity<List<User>> search(@Valid @RequestBody UserSearchCriteria usc) {
    return new ResponseEntity<>(
            userDao.findMatching(usc.getRoleType(), usc.getApplicationType(), usc.getCityDistrictId()),
            HttpStatus.OK);
  }

  @PostMapping(value = "/owners")
  public ResponseEntity<Map<Integer, User>> findApplicationOwners(@RequestBody List<Integer> applicationIds) {
    Map<Integer, User> users = userDao.findByApplicationIds(applicationIds);
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @GetMapping(value = "/role/{roleType}")
  public ResponseEntity<List<User>> findByRole(@PathVariable RoleType roleType) {
    return new ResponseEntity<>(userDao.findByRole(roleType), HttpStatus.OK);
  }

  @GetMapping(value = "/userName")
  public ResponseEntity<User> getUser(@RequestParam String userName) {
    User user = userDao.findByUserName(userName).orElseThrow(() -> new NoSuchEntityException("user.notFound", userName));
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<User> addUser(@RequestBody User user) {
    return new ResponseEntity<>(userDao.insert(user), HttpStatus.OK);
  }

  @PutMapping
  public ResponseEntity updateUser(@RequestBody User user) throws NoSuchEntityException {
    userDao.update(user);
    return new ResponseEntity(HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/lastLogin")
  public void setLastLogin(@PathVariable int id, @RequestBody ZonedDateTime loginTime) {
    userDao.setLastLogin(id, loginTime);
  }

  @PostMapping(value = "/find")
  public ResponseEntity<List<User>> findByIds(@RequestBody List<Integer> ids) {
    List<User> users = userDao.findByIds(ids);
    return new ResponseEntity<>(users, HttpStatus.OK);
  }
}