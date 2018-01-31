package fi.hel.allu.ui.security;

import com.google.common.collect.Sets;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.servicecore.security.TokenUtil;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.service.UserService;
import io.jsonwebtoken.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

public class TokenAuthenticationServiceTest {

  // Secret key DER creation commands
  // $ openssl genrsa -out private.pem 1024
  // $ openssl pkcs8 -topk8 -inform PEM -outform DER -in private.pem -nocrypt > private.der
  // $ base64 private.der
  private final String secretDerKeyBase64 = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALrgLYqW/c80txrah5Fu9syFJnvJelD1i3VsaW28pUrRWrqw25DnmKTcgSETIkeWRyYr2dUi2FC2NXxZwLVnlBmg2raoKSU3sIAbVBF+7aWTYvBRlj4ondfHa11mPveQDloHaahYHP0vYhpIYccSaOlRQSSsoYBQgnezwSgVuJ9/AgMBAAECgYBxRt6UOb6UUZIElx7CS48SnkubkZ+xX4YvoY2u1UEkvw7OR9JwXdKrbHZhxCVtW2y5eK9MgRi6pBh7zjQAQk8EyMlOqqZLouQ6/gk6mPMLmbNlZqHqNegKEtQIoxPhpiA/9ixe58Z+bfnjUKSDFCIHO7s9CAa285U9ADDsWHAfgQJBAN2zNcCqgJk2nDNGNBzU4xYJQsmwnYdXh0GUglNftltf+f9uS6o3xPL7Fm0LiLqqc6va9wduvqSynTKvRCGYwl8CQQDXybF3tVoarm/BaeVcIbn3Nys3m30V0mJx3+gioZETDme5Bq8SuWyImIyftDNvYwi926m5vmuqazSFBu6T4nbhAkB28KZt7wt/J2U1vPxIW45ZTC6gtjhNXBAchuhEgpz6+MrO1wWRFMp88WheqEl6m+fi016khi2RfqIHhzAuNh5RAkBXYRr16H/GEiC651CzEA0n6DUd3V63eWvXxN1ROK9wUgL6T5SRNniWj39SJDw3vJiLmOmPh2Y9qg+oVnhBdhDBAkApBlNBCymajXq7xRK8jfM4/Anz1Si93fciVp9LazsZHt87TXK0uv8tcRclIBLk+qGHB8kSlH394JHvGnT/9xAh";
  // Certificate creation command
  // $ openssl req -new -x509 -key private.pem
  private final String certificateStr = "MIIC2DCCAkGgAwIBAgIJAPgO7xhOmEEQMA0GCSqGSIb3DQEBCwUAMIGEMQswCQYDVQQGEwJGSTEQMA4GA1UECAwHVXVzaW1hYTERMA8GA1UEBwwISGVsc2lua2kxDzANBgNVBAoMBlZpbmNpdDERMA8GA1UECwwISGVsc2lua2kxDTALBgNVBAMMBHRlc3QxHTAbBgkqhkiG9w0BCQEWDnRlc3RAdmluY2l0LmZpMB4XDTE3MDEwMjExNDQxNloXDTE3MDIwMTExNDQxNlowgYQxCzAJBgNVBAYTAkZJMRAwDgYDVQQIDAdVdXNpbWFhMREwDwYDVQQHDAhIZWxzaW5raTEPMA0GA1UECgwGVmluY2l0MREwDwYDVQQLDAhIZWxzaW5raTENMAsGA1UEAwwEdGVzdDEdMBsGCSqGSIb3DQEJARYOdGVzdEB2aW5jaXQuZmkwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBALrgLYqW/c80txrah5Fu9syFJnvJelD1i3VsaW28pUrRWrqw25DnmKTcgSETIkeWRyYr2dUi2FC2NXxZwLVnlBmg2raoKSU3sIAbVBF+7aWTYvBRlj4ondfHa11mPveQDloHaahYHP0vYhpIYccSaOlRQSSsoYBQgnezwSgVuJ9/AgMBAAGjUDBOMB0GA1UdDgQWBBRxJbOi9vH6GoG9lXTqYqiyPOB0STAfBgNVHSMEGDAWgBRxJbOi9vH6GoG9lXTqYqiyPOB0STAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBCwUAA4GBACOD3ZaG2zJ73puWx8jeDj6Vu5mtZx3WFCTffMim9TaHRbWcJsym+y9TI+Mb6pKugHIai8uetFU9KXyRxu0NSqxlvp0TeOcl4DM4o1zGlrmUpTc0RyHqfHu6LsY+uhSp9W1hwKBjbsZ0HdwlF4Vuz0B2BGrWEndkmguxKK/WjJ3f";

  private UserService userService;
  private RestTemplate restTemplate;
  private ApplicationProperties applicationProperties;
  private TokenAuthenticationService tokenAuthenticationService;

  private final String clientId = "client123";
  private final String redirectUri = "https://redirect.uri";
  private final String code = "123";
  private final String codeBody = "client_id=" + clientId + "&redirect_uri=" + redirectUri + "&code=" + code + "&grant_type=authorization_code";
  private final String USER_NAME = "username";

  private final String oauth2TokenUrl = "https://token.url";
  private TokenAuthenticationService.TokenWrapper tokenWrapper;

  private UserJson userJson;
  private TokenUtil tokenUtil;

  @Before
  public void init() {
    applicationProperties = Mockito.mock(ApplicationProperties.class);
    userService = Mockito.mock(UserService.class);
    restTemplate = Mockito.mock(RestTemplate.class);

    Mockito.when(applicationProperties.getOauth2TokenUrl()).thenReturn(oauth2TokenUrl);
    Mockito.when(applicationProperties.getOauth2ClientId()).thenReturn(clientId);
    Mockito.when(applicationProperties.getOauth2RedirectUri()).thenReturn(redirectUri);
    Mockito.when(applicationProperties.getOauth2Certificate()).thenReturn(certificateStr);
    Mockito.when(applicationProperties.getJwtSecret()).thenReturn("test secret");
    Mockito.when(applicationProperties.getJwtExpirationHours()).thenReturn(12);
    tokenWrapper = new TokenAuthenticationService.TokenWrapper();
    tokenAuthenticationService = new TokenAuthenticationService(applicationProperties, restTemplate, userService);
    tokenAuthenticationService.initializePublicKey();

    List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
    roles.add(new SimpleGrantedAuthority(RoleType.ROLE_VIEW.toString()));
    roles.add(new SimpleGrantedAuthority(RoleType.ROLE_ADMIN.toString()));
    userJson = new UserJson(
        1,
        USER_NAME,
        "John Doe",
        "email",
        "Johtaja",
        true,
        null,
        Collections.emptyList(),
        Arrays.asList(RoleType.ROLE_VIEW, RoleType.ROLE_ADMIN),
        Collections.emptyList());

    tokenUtil = new TokenUtil(applicationProperties.getJwtSecret());
  }

  private String createToken(List<String> groups) throws Exception {

    KeyFactory privateKeyFactory = KeyFactory.getInstance("RSA");
    PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(Base64.decode(secretDerKeyBase64.getBytes()));
    PrivateKey privateKey = privateKeyFactory.generatePrivate(encodedKeySpec);

//    Left here on purpose. Helps a lot with debugging if such need occurs...
//    CertificateFactory f = CertificateFactory.getInstance("X.509");
//    X509Certificate certificate = (X509Certificate)f.generateCertificate(new ByteArrayInputStream(Base64.decode(certificateStr.getBytes())));
//    PublicKey pk = certificate.getPublicKey();
//    final Claims claims = Jwts.parser().setSigningKey(pk).parseClaimsJws(token).getBody();

    LocalDateTime dateTimeToConvert = LocalDateTime.now().plusHours(1);
    Date convertToDate = Date.from(dateTimeToConvert.atZone(ZoneId.systemDefault()).toInstant());

    String token = Jwts.builder()
        .setExpiration(convertToDate)
        .claim("winaccountname", USER_NAME)
        .claim("unique_name", "firstname lastname")
        .claim("email", "mail@some.fi")
        .claim("group", groups)
        .signWith(SignatureAlgorithm.RS256, privateKey)
        .compact();
    return token;
  }

  @Test
  public void testAuthenticateWithOAuth2CodeExistingUser() throws Exception {
    tokenWrapper.access_token = createToken(Collections.singletonList("sg_HKR_Allu"));
    Mockito.when(restTemplate.postForObject(oauth2TokenUrl, codeBody, TokenAuthenticationService.TokenWrapper.class)).thenReturn(tokenWrapper);
    Mockito.when(userService.findUserByUserName(USER_NAME)).thenReturn(userJson);
    Optional<UserJson> userJsonOpt = tokenAuthenticationService.authenticateWithOAuth2Code(code);
    Assert.assertEquals(userJson, userJsonOpt.get());
  }

  @Test
  public void testAuthenticateWithOAuth2CodeNewUser() throws Exception {
    tokenWrapper.access_token = createToken(Collections.singletonList("sg_HKR_Allu"));
    Mockito.when(restTemplate.postForObject(oauth2TokenUrl, codeBody, TokenAuthenticationService.TokenWrapper.class)).thenReturn(tokenWrapper);
    Mockito.when(userService.findUserByUserName(USER_NAME)).thenThrow(new NoSuchEntityException(""));
    Mockito.when(userService.addUser(Mockito.any(UserJson.class))).thenReturn(userJson);
    Optional<UserJson> userJsonOpt = tokenAuthenticationService.authenticateWithOAuth2Code(code);
    Assert.assertEquals(userJson, userJsonOpt.get());
  }

  @Test
  public void testAuthenticateWithOAuth2CodeNonAlluGroup() throws Exception {
    tokenWrapper.access_token = createToken(Collections.singletonList("not_expected_group"));
    Mockito.when(restTemplate.postForObject(oauth2TokenUrl, codeBody, TokenAuthenticationService.TokenWrapper.class)).thenReturn(tokenWrapper);
    Mockito.when(userService.findUserByUserName(USER_NAME)).thenThrow(new NoSuchEntityException(""));
    Optional<UserJson> userJsonOpt = tokenAuthenticationService.authenticateWithOAuth2Code(code);
    Assert.assertFalse(userJsonOpt.isPresent());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAuthenticateWithOAuth2CodeInvalidCode() throws Exception {
    tokenWrapper.access_token = createToken(Collections.singletonList("sg_HKR_Allu"));
    Mockito.when(restTemplate.postForObject(oauth2TokenUrl, codeBody, TokenAuthenticationService.TokenWrapper.class))
        .thenThrow(new IllegalArgumentException());
    tokenAuthenticationService.authenticateWithOAuth2Code(code);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateJWTWithoutPrinciple() {
    Set<GrantedAuthority> roles = Sets.newHashSet(new SimpleGrantedAuthority(RoleType.ROLE_VIEW.toString()));
    UserJson userNullId = new UserJson();
    tokenAuthenticationService.createTokenForUser(userNullId);
  }

  @Test
  public void testCreateValidJWT() {
    String token = tokenAuthenticationService.createTokenForUser(userJson);
    assertNotNull("token must not be null", token);
    JwtParser parser = Jwts.parser().setSigningKey(applicationProperties.getJwtSecret());
    assertTrue("token must be signed", parser.isSigned(token));
  }

  @Test
  public void testParseJWT() {
    String token = tokenAuthenticationService.createTokenForUser(userJson);
    User alluUser = (User) tokenUtil.parseUserFromToken(
        TokenUtil.PROPERTY_ROLE_ALLU,
        token);
    assertNotNull("User must not be null", alluUser);
    assertEquals(USER_NAME, alluUser.getUsername());
    assertEquals("", alluUser.getPassword());
    assertEquals(2, alluUser.getAuthorities().size());
    Set<GrantedAuthority> roles = Sets.newHashSet(
        new SimpleGrantedAuthority(RoleType.ROLE_ADMIN.toString()),
        new SimpleGrantedAuthority(RoleType.ROLE_VIEW.toString()));
    assertThat(roles, containsInAnyOrder(alluUser.getAuthorities().toArray()));
  }

  @Test(expected = ExpiredJwtException.class)
  public void testExpiredJWT() throws UnsupportedEncodingException {
    Mockito.when(applicationProperties.getJwtExpirationHours()).thenReturn(-1);
    String token = tokenAuthenticationService.createTokenForUser(userJson);
    tokenUtil.parseUserFromToken(
        TokenUtil.PROPERTY_ROLE_ALLU,
        token);
  }

  @Test(expected = SignatureException.class)
  public void testInvalidJWT() {
    String token = tokenAuthenticationService.createTokenForUser(userJson);
    TokenUtil withWrongSecret = new TokenUtil(applicationProperties.getJwtSecret() + "invalid!");
    withWrongSecret.parseUserFromToken(TokenUtil.PROPERTY_ROLE_ALLU, token);
  }
}
