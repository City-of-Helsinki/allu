package fi.hel.allu.external.controller;

import fi.hel.allu.external.config.ApplicationProperties;
import fi.hel.allu.external.service.ServerTokenAuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Properties;

@RestController
@RequestMapping("/token")
public class TokenController {

  private static final String GRANT_TYPE = "grant_type";
  private static final String CLIENT_CREDENTIALS = "client_credentials";
  private static final String AUTH_HEADER_NAME = "Authorization";
  private static final String AUTH_KEY_BASIC = "Basic ";

  private ServerTokenAuthenticationService serverTokenAuthenticationService;
  private ApplicationProperties applicationProperties;

  @Autowired
  public TokenController(ServerTokenAuthenticationService serverTokenAuthenticationService,
      ApplicationProperties applicationProperties) {
    this.serverTokenAuthenticationService = serverTokenAuthenticationService;
    this.applicationProperties = applicationProperties;
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Properties> create(@RequestHeader(AUTH_HEADER_NAME) String auth,
      @RequestParam(GRANT_TYPE) String grantType) {
    // Check authorization header and request grant type:
    if (auth == null || !auth.startsWith(AUTH_KEY_BASIC)
        || !auth.substring(AUTH_KEY_BASIC.length()).equals(applicationProperties.getServiceAuth())
        || !CLIENT_CREDENTIALS.equals(grantType)) {
      return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    return new ResponseEntity<>(serverTokenAuthenticationService.createServiceToken(), HttpStatus.OK);
  }

}
