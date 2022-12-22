package fi.hel.allu.ui.controller;

import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.service.UserService;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.config.Environment;
import fi.hel.allu.ui.security.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

// TODO: remove or replace this with something once dummy login is removed
@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  TokenAuthenticationService tokenAuthenticationService;
  @Autowired
  ApplicationProperties applicationProperties;
  @Autowired
  UserService userService;

  @PostMapping(value = "/login")
  public ResponseEntity<String> login(@RequestBody TempLogin user) {
    if (!(applicationProperties.getEnvironment() == Environment.DEV ||
          applicationProperties.getEnvironment() == Environment.TEST)) {
      return new ResponseEntity<>("Login forbidden.", HttpStatus.FORBIDDEN);
    }
    UserJson userJson = userService.findUserByUserName(user.userName);
    userService.setLastLogin(userJson.getId(), ZonedDateTime.now());
    return new ResponseEntity<>(tokenAuthenticationService.createTokenForUser(userJson), HttpStatus.OK);
  }

  /**
   * Using dummy login as long as OAuth2 service is missing from Allu.
   */
  public static class TempLogin {
    public String userName;
  }
}