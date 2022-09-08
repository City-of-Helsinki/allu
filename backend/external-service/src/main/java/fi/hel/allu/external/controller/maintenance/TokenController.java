package fi.hel.allu.external.controller.maintenance;

import fi.hel.allu.external.config.ApplicationProperties;
import fi.hel.allu.external.service.ServerTokenAuthenticationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Properties;

@RestController
@RequestMapping("/token")
@Tag(name = "Token", description = "Used only for maintenance authentication. Can be only used by curl")
public class TokenController {

  private static final String GRANT_TYPE = "grant_type";
  private static final String CLIENT_CREDENTIALS = "client_credentials";
  private static final String AUTH_HEADER_NAME = "Authorization";
  private static final String AUTH_KEY_BASIC = "Basic ";

  private final ServerTokenAuthenticationService serverTokenAuthenticationService;
  private final ApplicationProperties applicationProperties;

  public TokenController(ServerTokenAuthenticationService serverTokenAuthenticationService,
      ApplicationProperties applicationProperties) {
    this.serverTokenAuthenticationService = serverTokenAuthenticationService;
    this.applicationProperties = applicationProperties;
  }

  @PostMapping
  public ResponseEntity<Properties> create(@RequestHeader(value = AUTH_HEADER_NAME) String auth,
      @RequestParam(GRANT_TYPE) String grantType) {
    if (applicationProperties.getServiceAuth().equals(basicAuthToken(auth)) && CLIENT_CREDENTIALS.equals(grantType)) {
      return new ResponseEntity<>(serverTokenAuthenticationService.createServiceToken(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

  }

  /*
   * Check that given Authorization: header is Basic kind and return its token
   */
  private String basicAuthToken(String auth) {
    if (auth == null || !auth.startsWith(AUTH_KEY_BASIC)) {
      return null;
    } else {
      return auth.substring(AUTH_KEY_BASIC.length());
    }
  }
}
