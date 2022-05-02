package fi.hel.allu.ui.security;

import java.util.*;

import fi.hel.allu.servicecore.security.TokenUtil;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.security.AadService;
import fi.hel.allu.servicecore.security.domain.AdClaims;
import fi.hel.allu.servicecore.service.UserService;
import fi.hel.allu.ui.config.ApplicationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TokenAuthenticationServiceTest {

  private UserService userService;
  private ApplicationProperties applicationProperties;
  private TokenAuthenticationService tokenAuthenticationService;
  private AadService aadService;

  private final String code = "123";
  private final String USER_NAME = "username";
  private final String REAL_NAME = "John sDoe";
  private final String EMAIL = "john.doe@foo.bar";
  private final String ALLU_GROUP_ID = "124-abc-345";

  private UserJson userJson;
  private Claims claims;
  private AdClaims adClaims;
  private TokenUtil tokenUtil;

  @Before
  public void init() {
    applicationProperties = Mockito.mock(ApplicationProperties.class);
    userService = Mockito.mock(UserService.class);
    aadService = Mockito.mock(AadService.class);
    claims = Mockito.mock(Claims.class);
    adClaims = new AdClaims(claims);

    Mockito.when(applicationProperties.getJwtSecret()).thenReturn("LookslikeWhiteSpaceAreNotAcceptedOnBase64Encoding" +
      "AlsoThisNeedsToBeOver516bitLonSoNowImWritingLongAssTextThatFulfillsCriteria");
    Mockito.when(applicationProperties.getAlluAdGroupId()).thenReturn(ALLU_GROUP_ID);
    Mockito.when(applicationProperties.getJwtExpirationHours()).thenReturn(12);
    Mockito.when(claims.get("upn", String.class)).thenReturn(USER_NAME);
    Mockito.when(claims.get("name", String.class)).thenReturn(REAL_NAME);
    Mockito.when(claims.get("email", String.class)).thenReturn(EMAIL);

    tokenAuthenticationService = new TokenAuthenticationService(applicationProperties, userService, aadService);

    List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
    roles.add(new SimpleGrantedAuthority(RoleType.ROLE_VIEW.toString()));
    roles.add(new SimpleGrantedAuthority(RoleType.ROLE_ADMIN.toString()));
    userJson = new UserJson(
        1,
        USER_NAME,
        REAL_NAME,
        EMAIL,
        "123-321",
        "Johtaja",
        true,
        null,
        Collections.emptyList(),
        Arrays.asList(RoleType.ROLE_VIEW, RoleType.ROLE_ADMIN),
        Collections.emptyList());
  }

  @Test
  public void testAuthenticateWithOAuth2CodeExistingUser() throws Exception {
    Mockito.when(aadService.exchangeCodeForToken(code)).thenReturn(adClaims);
    Mockito.when(userService.findUserByUserName(USER_NAME)).thenReturn(userJson);
    Optional<UserJson> userJsonOpt = tokenAuthenticationService.authenticateWithOAuth2Code(code);
    Assert.assertEquals(userJson, userJsonOpt.get());
  }

  @Test
  public void testAuthenticateWithOAuth2CodeNewUser() throws Exception {
    Mockito.when(aadService.exchangeCodeForToken(code)).thenReturn(adClaims);
    Mockito.when(userService.findUserByUserName(USER_NAME)).thenThrow(new NoSuchEntityException(""));
    Mockito.when(userService.addUser(Mockito.any(UserJson.class))).thenReturn(userJson);
    Mockito.when(claims.get("groups", List.class)).thenReturn(Collections.singletonList(ALLU_GROUP_ID));
    Optional<UserJson> userJsonOpt = tokenAuthenticationService.authenticateWithOAuth2Code(code);
    Assert.assertEquals(userJson, userJsonOpt.get());
  }

  @Test
  public void testAuthenticateWithOAuth2CodeNonAlluGroup() throws Exception {
    Mockito.when(aadService.exchangeCodeForToken(code)).thenReturn(adClaims);
    Mockito.when(userService.findUserByUserName(USER_NAME)).thenThrow(new NoSuchEntityException(""));
    Mockito.when(claims.get("groups", List.class)).thenReturn(Collections.emptyList());
    Optional<UserJson> userJsonOpt = tokenAuthenticationService.authenticateWithOAuth2Code(code);
    Assert.assertFalse(userJsonOpt.isPresent());
  }

  public void testAuthenticateWithOAuth2CodeInvalidCode() throws Exception {
    Mockito.when(aadService.exchangeCodeForToken(code)).thenThrow(new IllegalArgumentException());
    Optional<UserJson> userJsonOpt = tokenAuthenticationService.authenticateWithOAuth2Code(code);
    Assert.assertFalse(userJsonOpt.isPresent());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateJWTWithoutPrinciple() {
    UserJson userNullId = new UserJson();
    tokenAuthenticationService.createTokenForUser(userNullId);
  }

  @Test
  public void testCreateValidJWT() {
    String token = tokenAuthenticationService.createTokenForUser(userJson);
    assertNotNull("token must not be null", token);
    JwtParser parser = Jwts.parserBuilder().setSigningKey
      (Keys.hmacShaKeyFor(Decoders.BASE64.decode(applicationProperties.getJwtSecret()))).build();
    assertTrue("token must be signed", parser.isSigned(token));
  }


}
