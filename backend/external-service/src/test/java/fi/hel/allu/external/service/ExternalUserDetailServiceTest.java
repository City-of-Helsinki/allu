package fi.hel.allu.external.service;

import fi.hel.allu.common.domain.types.ExternalRoleType;
import fi.hel.allu.model.domain.user.ExternalUser;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class ExternalUserDetailServiceTest {

	private static final String EXTERNAL_USER_URL = "http://externaluser/{username}";
	private static final ExternalRoleType ROLE = ExternalRoleType.ROLE_TRUSTED_PARTNER;
	@Mock
	private RestTemplate restTemplate;
	@Mock
	private ApplicationProperties applicationProperties;

	private ExternalUserDetailService service;

	private ExternalUser user;

	@BeforeEach
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
		Mockito.when(restTemplate.getForEntity(EXTERNAL_USER_URL, ExternalUser.class, user.getUsername()))
				.thenReturn(response);
	}

	@Test
	void shouldReturnExternalUserDetails() {
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
	void shouldDisableAccountIfNotActive() {
		user.setActive(false);
		UserDetails userDetails = service.loadUserByUsername(user.getUsername());
		assertFalse(userDetails.isEnabled());
		assertFalse(userDetails.isAccountNonLocked());

	}

	@Test
	void shouldSetAccountExpired() {
		user.setExpirationTime(ZonedDateTime.now().minusHours(1));
		UserDetails userDetails = service.loadUserByUsername(user.getUsername());
		assertFalse(userDetails.isAccountNonExpired());
		assertFalse(userDetails.isCredentialsNonExpired());
	}
}
