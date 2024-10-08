package fi.hel.allu.ui.controller;

import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.service.UserService;
import fi.hel.allu.ui.security.TokenAuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Support for AD  OAuth2 authentication.
 */
@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {

  private static final Logger logger = LoggerFactory.getLogger(OAuth2Controller.class);

  @Autowired
  TokenAuthenticationService tokenAuthenticationService;
  @Autowired
  UserService userService;

  @GetMapping(value = "/")
  public ResponseEntity<String> login(@RequestParam String code) {
    Optional<UserJson> userJsonOpt = tokenAuthenticationService.authenticateWithOAuth2Code(code);
    if (userJsonOpt.isPresent()) {
      UserJson userJson = userJsonOpt.get();
      if (userJson.isActive()) {
        userService.setLastLogin(userJson.getId(), ZonedDateTime.now());
        return new ResponseEntity<String>(tokenAuthenticationService.createTokenForUser(userJson), HttpStatus.OK);
      } else {
        logger.info("Attempt to login using inactive user account: {}" + userJson.getUserName());
        throw new LockedException("User account is not active");
      }
    } else {
      logger.warn("Attempt to login using invalid OAuth2 code: {}", code);
      throw new BadCredentialsException("Unauthorized");
    }
  }
}