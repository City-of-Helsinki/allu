package fi.hel.allu.supervision.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.supervision.api.security.TokenAuthenticationService;

@RestController
@RequestMapping("/v1/login")
@Tag(name = "Authentication")
public class AuthenticationController {

  private final TokenAuthenticationService tokenAuthenticationService;

  public AuthenticationController(TokenAuthenticationService tokenAuthenticationService) {
    this.tokenAuthenticationService = tokenAuthenticationService;
  }

  @Operation(summary = "Authenticate with AD token")
  @PostMapping
  public ResponseEntity<String> login(@RequestParam @Parameter(description = "AD JWT", required = true) String adToken) {
    return ResponseEntity.ok(tokenAuthenticationService.loginWithAdToken(adToken));
  }

}
