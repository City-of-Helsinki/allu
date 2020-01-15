package fi.hel.allu.external.api.service;

import java.time.ZonedDateTime;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.domain.types.ExternalRoleType;
import fi.hel.allu.external.service.ExternalUserDetailService;
import fi.hel.allu.model.domain.user.ExternalUser;
import fi.hel.allu.servicecore.config.ApplicationProperties;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ExternalUserDetailServiceTest {

  private static final String EXTERNAL_USER_URL = "http://externaluser/{username}";
  private static final ExternalRoleType ROLE = ExternalRoleType.ROLE_TRUSTED_PARTNER;
  @Mock
  private RestTemplate restTemplate;
  @Mock
  private ApplicationProperties applicationProperties;

  private ExternalUserDetailService service;

  private ExternalUser user;

  @Before
  public void setup() {
    service = new ExternalUserDetailService(restTemplate, applicationProperties);
    user = new ExternalUser();
    user.setUsername("username");
    user.setPassword("password");
    user.setActive(true);
    user.setExpirationTime(ZonedDateTime.now().plusDays(3));
    user.setAssignedRoles(Collections.singletonList(ROLE));
    ResponseEntity<ExternalUser> response = ResponseEntity.ok(user);
    Mockito.when(applicationProperties.getExternalUserByUserNameUrl()).thenReturn(EXTERNAL_USER_URL);
    Mockito.when(restTemplate.getForEntity(Mockito.eq(EXTERNAL_USER_URL),  Mockito.eq(ExternalUser.class), Mockito.eq(user.getUsername()))).thenReturn(response);
  }

  @Test
  public void shouldReturnExternalUserDetails() {
    UserDetails userDetails = service.loadUserByUsername(user.getUsername());
    assertEquals(user.getUsername(), userDetails.getUsername());
    assertTrue(userDetails.isAccountNonExpired());
    assertTrue(userDetails.isAccountNonLocked());
    assertTrue(userDetails.isCredentialsNonExpired());
    assertTrue(userDetails.isEnabled());
    assertEquals(1, userDetails.getAuthorities().size());
    assertEquals(ROLE.name(), userDetails.getAuthorities().iterator().next().getAuthority());
  }

  @Test
  public void shouldDisableAccountIfNotActive() {
    user.setActive(false);
    UserDetails userDetails = service.loadUserByUsername(user.getUsername());
    assertFalse(userDetails.isEnabled());
    assertFalse(userDetails.isAccountNonLocked());

  }

  @Test
  public void shouldSetAccountExpired() {
    user.setExpirationTime(ZonedDateTime.now().minusHours(1));
    UserDetails userDetails = service.loadUserByUsername(user.getUsername());
    assertFalse(userDetails.isAccountNonExpired());
    assertFalse(userDetails.isCredentialsNonExpired());
  }
}
