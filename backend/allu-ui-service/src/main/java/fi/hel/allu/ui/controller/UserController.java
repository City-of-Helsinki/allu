package fi.hel.allu.ui.controller;

import fi.hel.allu.ui.domain.UserJson;
import fi.hel.allu.ui.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing Allu users.
 */
@RestController
@RequestMapping("/users")
public class UserController {

  private UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @RequestMapping(method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<List<UserJson>> getUsers() {
    return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.OK);
  }

  @RequestMapping(value = "/active", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<UserJson>> getActiveUsers() {
    return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.OK);
  }

  @RequestMapping(value = "/userName/{userName}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<UserJson> findByUserName(@PathVariable String userName) {
    UserJson user = userService.findUserByUserName(userName);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<UserJson> addUser(@RequestBody UserJson user) {
    return new ResponseEntity<>(userService.addUser(user), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity updateUser(@RequestBody UserJson user) {
    userService.updateUser(user);
    return new ResponseEntity(HttpStatus.OK);
  }
}
