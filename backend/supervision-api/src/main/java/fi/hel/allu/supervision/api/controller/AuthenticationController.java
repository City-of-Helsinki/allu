package fi.hel.allu.supervision.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.supervision.api.security.TokenAuthenticationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/login")
@Api(tags = "Authentication")
public class AuthenticationController {

  @Autowired
  private TokenAuthenticationService tokenAuthenticationService;

  @ApiOperation(value = "Authenticate with AD token")
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<String> login(@RequestParam @ApiParam(value = "AD JWT", required = true) String adToken) {
    return ResponseEntity.ok(tokenAuthenticationService.loginWithAdToken(adToken));
  }

}
