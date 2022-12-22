package fi.hel.allu.ui.controller;

import fi.hel.allu.common.domain.UserSearchCriteria;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.service.UserService;
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

  /**
   * Check is caller autheticated properly and return HTTP 200, if ok. Otherwise authentication fails before execution of this method, which
   * tells caller that authentication is not properly done.
   *
   * @return  Nothing.
   */
  @GetMapping(value = "/isauthenticated")
  @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION','ROLE_PROCESS_APPLICATION','ROLE_DECISION','ROLE_SUPERVISE','ROLE_INVOICING','ROLE_DECLARANT', 'ROLE_VIEW','ROLE_ADMIN', 'ROLE_MANAGE_SURVEY')")
  public ResponseEntity<Void> checkToken() {
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<List<UserJson>> getUsers() {
    return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.OK);
  }

  @GetMapping(value = "/active")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<UserJson>> getActiveUsers() {
    return new ResponseEntity<>(userService.findAllActiveUsers(), HttpStatus.OK);
  }

  @GetMapping(value = "/userName/{userName}")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<UserJson> findByUserName(@PathVariable String userName) {
    UserJson user = userService.findUserByUserName(userName);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @GetMapping(value = "/{id}")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<UserJson> getUser(@PathVariable Integer id) {
    return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
  }

  @GetMapping(value = "/role/{roleType}")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<UserJson>> findByRole(@PathVariable RoleType roleType) {
    return new ResponseEntity<>(userService.findUserByRole(roleType), HttpStatus.OK);
  }

  @GetMapping(value = "/current")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<UserJson> getCurrentUser() {
    UserJson user = userService.getCurrentUser();
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @PostMapping(value = "/search")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<UserJson>> search(@RequestBody UserSearchCriteria usc) {
    return new ResponseEntity<>(userService.search(usc), HttpStatus.OK);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<UserJson> addUser(@RequestBody UserJson user) {
    return new ResponseEntity<>(userService.addUser(user), HttpStatus.OK);
  }

  @PutMapping
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity updateUser(@RequestBody UserJson user) {
    userService.updateUser(user);
    return new ResponseEntity(userService.findUserById(user.getId()), HttpStatus.OK);
  }
}