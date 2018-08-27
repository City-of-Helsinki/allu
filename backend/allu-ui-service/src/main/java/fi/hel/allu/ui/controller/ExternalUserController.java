package fi.hel.allu.ui.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.servicecore.domain.ExternalUserJson;
import fi.hel.allu.servicecore.service.ExternalUserService;

/**
 * Controller for managing Allu users.
 */
@RestController
@RequestMapping("/externalusers")
public class ExternalUserController {

  private ExternalUserService userService;

  @Autowired
  public ExternalUserController(ExternalUserService userService) {
    this.userService = userService;
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<ExternalUserJson> findById(@PathVariable int id) {
    return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<List<ExternalUserJson>> getUsers() {
    return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<ExternalUserJson> addUser(@RequestBody @Validated(ExternalUserJson.Create.class) ExternalUserJson user) {
    return new ResponseEntity<>(userService.addUser(user), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<ExternalUserJson> updateUser(@RequestBody ExternalUserJson user) {
    userService.updateUser(user);
    return new ResponseEntity<>(userService.findUserById(user.getId()), HttpStatus.OK);
  }
}
