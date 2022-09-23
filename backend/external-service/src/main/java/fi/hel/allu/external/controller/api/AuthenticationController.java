package fi.hel.allu.external.controller.api;

import fi.hel.allu.external.config.ApplicationProperties;
import fi.hel.allu.external.domain.LoginExt;
import fi.hel.allu.servicecore.security.TokenUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/v1/login", "/v2/login"})
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final ApplicationProperties applicationProperties;

    public AuthenticationController(AuthenticationManager authenticationManager,
                                    ApplicationProperties applicationProperties) {
        this.authenticationManager = authenticationManager;
        this.applicationProperties = applicationProperties;
    }

    @PostMapping
    public ResponseEntity<String> login(@Valid @RequestBody LoginExt loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        Map<String, Object> roleMap = Collections.singletonMap(TokenUtil.PROPERTY_ROLE_ALLU_PUBLIC,
                                                               user.getAuthorities().stream()
                                                                       .map(GrantedAuthority::getAuthority)
                                                                       .collect(Collectors.toList()));
        String token = new TokenUtil(applicationProperties.getJwtSecret()).createToken(
                ZonedDateTime.now().plusMinutes(applicationProperties.getJwtExpirationTime()), user.getUsername(),
                roleMap);
        return ResponseEntity.ok(token);
    }
}
