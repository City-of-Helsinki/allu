package fi.hel.allu.ui.security;

import com.google.common.collect.Sets;
import fi.hel.allu.common.types.RoleType;
import fi.hel.allu.ui.domain.UserJson;
import fi.hel.allu.ui.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

public class TokenHandlerTest {
  private TokenHandler tokenHandler;
  private UserService userService;
  private UserJson user;
  private String secret = "Allun salainen avain";

  @Before
  public void setUp() {
    tokenHandler = new TokenHandler(secret, 12);
    List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
    roles.add(new SimpleGrantedAuthority(RoleType.ROLE_VIEW.toString()));
    roles.add(new SimpleGrantedAuthority(RoleType.ROLE_ADMIN.toString()));
    user = new UserJson(
        1,
        "johndoe",
        "John Doe",
        "email",
        "Johtaja",
        true,
        null,
        Collections.emptyList(),
        Arrays.asList(RoleType.ROLE_VIEW, RoleType.ROLE_ADMIN),
        Collections.emptyList());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateTokenWithoutPrinciple() {
    Set<GrantedAuthority> roles = Sets.newHashSet(new SimpleGrantedAuthority(RoleType.ROLE_VIEW.toString()));
    UserJson userNullId = new UserJson();
    tokenHandler.createTokenForUser(userNullId);
  }


  @Test
  public void testCreateValidToken() {
    String token = tokenHandler.createTokenForUser(user);
    assertNotNull("token must not be null", token);
    JwtParser parser = Jwts.parser().setSigningKey(secret);
    assertTrue("token must be signed", parser.isSigned(token));
  }

  @Test
  public void testParseToken() {
    String token = tokenHandler.createTokenForUser(user);
    AlluUser alluUser = (AlluUser) tokenHandler.parseUserFromToken(token);
    assertNotNull("User must not be null", alluUser);
    assertEquals("email", alluUser.getEmailAddress());
    assertEquals("johndoe", alluUser.getUsername());
    assertEquals("", alluUser.getPassword());
    assertEquals(2, alluUser.getAuthorities().size());
    Set<GrantedAuthority> roles = Sets.newHashSet(
        new SimpleGrantedAuthority(RoleType.ROLE_ADMIN.toString()),
        new SimpleGrantedAuthority(RoleType.ROLE_VIEW.toString()));
    assertThat(roles, containsInAnyOrder(alluUser.getAuthorities().toArray()));
  }

  @Test(expected = ExpiredJwtException.class)
  public void testExpiredToken() {
    tokenHandler = new TokenHandler(secret, -1);
    String token = tokenHandler.createTokenForUser(user);
    tokenHandler.parseUserFromToken(token);
  }
}
