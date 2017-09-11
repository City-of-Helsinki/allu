package fi.hel.allu.ui.controller;

import fi.hel.allu.servicecore.domain.ExternalUserJson;
import fi.hel.allu.servicecore.service.ExternalUserService;
import fi.hel.allu.ui.config.ApplicationProperties;
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
  private ApplicationProperties applicationProperties;

  @Autowired
  public ExternalUserController(ExternalUserService userService, ApplicationProperties applicationProperties) {
    this.userService = userService;
    this.applicationProperties = applicationProperties;
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
  public ResponseEntity<ExternalUserJson> addUser(@RequestBody ExternalUserJson user) {
    return new ResponseEntity<>(userService.addUser(applicationProperties.getJwtSecretExternalService(), user), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<ExternalUserJson> updateUser(@RequestBody ExternalUserJson user) {
    userService.updateUser(applicationProperties.getJwtSecretExternalService(), user);
    return new ResponseEntity(userService.findUserById(user.getId()), HttpStatus.OK);
  }
}
