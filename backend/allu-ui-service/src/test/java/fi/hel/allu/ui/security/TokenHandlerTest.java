package fi.hel.allu.ui.security;

import com.google.common.collect.Sets;
import fi.hel.allu.ui.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

public class TokenHandlerTest {
    private TokenHandler tokenHandler;
    private UserService userService;
    private AlluUser user;
    private String secret = "Allun salainen avain";

    @Before
    public void setUp() {
        userService = new UserService();
        tokenHandler = new TokenHandler(secret, 12, userService);
        List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority(Roles.ROLE_VIEW.toString()));
        roles.add(new SimpleGrantedAuthority(Roles.ROLE_ADMIN.toString()));
        user = new AlluUser("johndoe", "pwd", roles, "email");
        userService.addUser(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTokenWithoutPrinciple() {
        Set<GrantedAuthority> roles = Sets.newHashSet(new SimpleGrantedAuthority(Roles.ROLE_VIEW.toString()));
        AlluUser userNullId = new AlluUser(null, "pwd", roles, "email");
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
        assertEquals("johndoe", alluUser.getPassword());
        assertEquals(2, alluUser.getAuthorities().size());
        Set<GrantedAuthority> roles = Sets.newHashSet(
                new SimpleGrantedAuthority(Roles.ROLE_ADMIN.toString()),
                new SimpleGrantedAuthority(Roles.ROLE_VIEW.toString()));
        assertThat(roles, containsInAnyOrder(alluUser.getAuthorities().toArray()));
    }

    @Test(expected = ExpiredJwtException.class)
    public void testExpiredToken() {
        tokenHandler = new TokenHandler(secret, -1, userService);
        String token = tokenHandler.createTokenForUser(user);
        tokenHandler.parseUserFromToken(token);

    }
}
