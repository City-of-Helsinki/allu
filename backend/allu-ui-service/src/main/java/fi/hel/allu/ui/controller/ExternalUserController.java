package fi.hel.allu.ui.controller;

import fi.hel.allu.servicecore.domain.ExternalUserJson;
import fi.hel.allu.servicecore.service.ExternalUserService;
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
@RequestMapping("/externalusers")
public class ExternalUserController {

  private ExternalUserService userService;

  @Autowired
  public ExternalUserController(ExternalUserService userService) {
    this.userService = userService;
  }

  @RequestMapping(method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<List<ExternalUserJson>> getUsers() {
    return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<ExternalUserJson> addUser(@RequestBody ExternalUserJson user) {
    return new ResponseEntity<>(userService.addUser(user), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<ExternalUserJson> updateUser(@RequestBody ExternalUserJson user) {
    userService.updateUser(user);
    return new ResponseEntity(user, HttpStatus.OK);
  }
}
