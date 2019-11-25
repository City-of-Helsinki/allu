package fi.hel.allu.external.api.controller;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.external.config.ApplicationProperties;
import fi.hel.allu.external.domain.LoginExt;
import fi.hel.allu.servicecore.security.TokenUtil;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v1/login")
@Api(tags = "Authentication")
public class AuthenticationController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private ApplicationProperties applicationProperties;



  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<String> login(@Valid @RequestBody LoginExt loginRequest) {
      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      loginRequest.getUsername(),
                      loginRequest.getPassword()
              )
      );
      SecurityContextHolder.getContext().setAuthentication(authentication);
      User user = (User) authentication.getPrincipal();
      Map<String, Object> roleMap = Collections.singletonMap(
          TokenUtil.PROPERTY_ROLE_ALLU_PUBLIC,
          user.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toList()));
      String token = new TokenUtil(applicationProperties.getJwtSecret()).createToken(
          ZonedDateTime.now().plusMinutes(applicationProperties.getJwtExpirationTime()),
          user.getUsername(),
          roleMap);
      return ResponseEntity.ok(token);
  }
}
