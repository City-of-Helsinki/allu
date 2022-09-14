package fi.hel.allu.supervision.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.supervision.api.security.TokenAuthenticationService;

@RestController
@RequestMapping("/v1/login")
@Tag(name = "Authentication")
public class AuthenticationController {

  @Autowired
  private TokenAuthenticationService tokenAuthenticationService;

  @Operation(summary = "Authenticate with AD token")
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<String> login(@RequestParam @Parameter(description = "AD JWT", required = true) String adToken) {
    return ResponseEntity.ok(tokenAuthenticationService.loginWithAdToken(adToken));
  }

}
