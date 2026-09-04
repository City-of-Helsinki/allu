package fi.hel.allu.external.security;

import fi.hel.allu.external.config.SecurityConfig;
import fi.hel.allu.external.service.ExternalUserDetailService;
import fi.hel.allu.external.service.ServerTokenAuthenticationService;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for {@link SecurityConfig}'s {@code SecurityFilterChain}, covering an authenticated
 * request with the required role (200), an unauthenticated request (401) and an authenticated request
 * missing the required role (403). The Spring context is limited to {@link SecurityConfig} plus a
 * minimal, test-only controller so unrelated production controllers and their heavy dependency graphs
 * are never instantiated. {@link UserDetailsService} is mocked since it is only exercised via
 * {@code AuthenticationController#login}, not by the JWT filter-chain scenarios exercised here.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SecurityConfig.class, SecurityConfigTest.TestController.class,
    SecurityConfigTest.TestPasswordEncoderConfig.class,
    org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class SecurityConfigTest {

  private static final String SERVICE_ONLY_PATH = "/test/service-only";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ApplicationProperties applicationProperties;

  @MockBean
  private ServerTokenAuthenticationService tokenAuthenticationService;

  @MockBean
  private ExternalUserDetailService externalUserDetailService;

  @Before
  public void init() {
    when(applicationProperties.getAnonymousAccessPaths()).thenReturn(Collections.emptyList());
  }

  @Test
  public void authenticatedRequestWithRequiredRoleSucceeds() throws Exception {
    when(tokenAuthenticationService.isEmptyAuthentication(any(HttpServletRequest.class))).thenReturn(false);
    when(tokenAuthenticationService.getAuthentication(any(HttpServletRequest.class)))
        .thenReturn(authenticationWithRole("ROLE_SERVICE"));

    mockMvc.perform(get(SERVICE_ONLY_PATH).header("Authorization", "Bearer test-token"))
        .andExpect(status().isOk());
  }

  @Test
  public void unauthenticatedRequestIsRejectedWith401() throws Exception {
    when(tokenAuthenticationService.isEmptyAuthentication(any(HttpServletRequest.class))).thenReturn(true);
    when(tokenAuthenticationService.isAnonymousAccessAllowedForPath(anyString())).thenReturn(false);

    mockMvc.perform(get(SERVICE_ONLY_PATH))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void authenticatedRequestWithoutRequiredRoleIsRejectedWith403() throws Exception {
    when(tokenAuthenticationService.isEmptyAuthentication(any(HttpServletRequest.class))).thenReturn(false);
    when(tokenAuthenticationService.getAuthentication(any(HttpServletRequest.class)))
        .thenReturn(authenticationWithRole("ROLE_ADMIN"));

    mockMvc.perform(get(SERVICE_ONLY_PATH).header("Authorization", "Bearer test-token"))
        .andExpect(status().isForbidden());
  }

  private static Authentication authenticationWithRole(String role) {
    List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
    return new UsernamePasswordAuthenticationToken("testuser", "N/A", authorities);
  }

  @RestController
  static class TestController {
    @GetMapping(SERVICE_ONLY_PATH)
    @PreAuthorize("hasAnyRole('ROLE_SERVICE')")
    public ResponseEntity<String> serviceOnly() {
      return ResponseEntity.ok("ok");
    }
  }

  @org.springframework.context.annotation.Configuration
  static class TestPasswordEncoderConfig {
    @org.springframework.context.annotation.Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
      return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
  }
}
