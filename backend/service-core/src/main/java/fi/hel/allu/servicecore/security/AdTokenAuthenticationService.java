package fi.hel.allu.servicecore.security;

import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * Base class for AD authentication services
 * @author User
 *
 */
public abstract class AdTokenAuthenticationService extends AuthenticationServiceInterface {

  private static final String OAUTH2_CLIENT_ID_PARAM = "client_id";
  private static final String OAUTH2_REDIRECT_URI_PARAM = "redirect_uri";
  private static final String OAUTH2_CODE_PARAM = "code";
  private static final String OAUTH2_GRANT_TYPE= "grant_type=authorization_code";
  // AD Token fields
  private static final String AD_USER_NAME = "winaccountname";
  private static final String AD_REAL_NAME = "unique_name";
  private static final String AD_EMAIL = "email";
  private static final String AD_GROUP = "group";
  private static final String AD_ALLU_GROUP_NAME = "sg_HKR_Allu";

  private static final Logger logger = LoggerFactory.getLogger(AdTokenAuthenticationService.class);

  private AdAuthenticationProperties properties;
  private RestTemplate restTemplate;
  private UserService userService;

  private PublicKey publicKey;
  private TokenUtil tokenUtil;

  protected AdTokenAuthenticationService(AdAuthenticationProperties properties, RestTemplate restTemplate, UserService userService) {
    this.properties = properties;
    this.restTemplate = restTemplate;
    this.userService = userService;
    this.tokenUtil = new TokenUtil(properties.getJwtSecret());
  }

  @PostConstruct
  public void initializePublicKey() {
    try {
      CertificateFactory f = CertificateFactory.getInstance("X.509");
      X509Certificate certificate = (X509Certificate) f.generateCertificate(
          new ByteArrayInputStream(Base64.decode(properties.getOauth2Certificate().getBytes())));
      publicKey = certificate.getPublicKey();
    } catch (CertificateException e) {
      logger.error("Unable to initialize public key", e);
    }
  }

  @Override
  public Authentication getAuthentication(HttpServletRequest request) {
    if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
      logger.debug("OPTIONS is always allowed");
      return new UserAuthentication(new User("options", "", Collections.<GrantedAuthority>emptySet()));
    }
    final String token = request.getHeader(AUTH_HEADER_NAME);
    if (token != null && token.startsWith("Bearer ")) {
      final User user = tokenUtil.parseUserFromToken(TokenUtil.PROPERTY_ROLE_ALLU, token.replaceFirst("^Bearer ", ""));
      if (user != null) {
        return new UserAuthentication(user);
      }
    }
    return null;
  }

  /**
   * Create a token using the given parameters and sign it using the application’s secret key.
   *
   * @param user User that contains principal that can be used to identify the user related to the JWT.
   * @return signed token
   */
  public String createTokenForUser(UserJson user) {
    if (user == null || user.getUserName() == null || user.getUserName().trim().length() == 0) {
      throw new IllegalArgumentException("User principal name must not be null");
    }

    ZonedDateTime dateTimeToConvert = ZonedDateTime.now().plusHours(properties.getJwtExpirationHours());
    Map<String, Object> propertyNameToValue = new HashMap<>();
    propertyNameToValue.put(TokenUtil.PROPERTY_ROLE_ALLU, user.getAssignedRoles());
    propertyNameToValue.put(TokenUtil.PROPERTY_EMAIL, user.getEmailAddress());
    return tokenUtil.createToken(dateTimeToConvert, user.getUserName(), propertyNameToValue);
  }

  /**
   * Authenticates user with OAuth2 code against AD. If user does not exist in Allu user database and user belongs to allu group in AD,
   * user is automatically added to Allu user database with viewing rights.
   *
   * @param   code  OAuth2 authorization code grant code.
   * @return  User data if login using code was successful. Otherwise nothing.
   */
  public Optional<UserJson> authenticateWithOAuth2Code(String code) {
    String adToken = exchangeCodeForToken(code);
    return authenticateWithAdToken(adToken);
  }

  protected Optional<UserJson> authenticateWithAdToken(String adToken) {
    AdJwtTokenFields tokenFields = parseToken(adToken);

    UserJson userJson = null;
    String username = StringUtils.lowerCase(tokenFields.winAccountName);
    try {
      userJson = userService.findUserByUserName(username);
    } catch (NoSuchEntityException e) {
      if (tokenFields.hasAlluGroup) {
        userJson = new UserJson(
            null,
            username,
            tokenFields.uniqueName,
            tokenFields.email,
            "", // phone
            "", // using empty value as title, because title is required. However, the correct title is unknown at this point
            true,
            null,
            Collections.emptyList(),
            Collections.singletonList(RoleType.ROLE_VIEW),
            Collections.emptyList());
        userJson = userService.addUser(userJson);
        logger.info("Automatically added new user from OAuth2 with user name {}", userJson.getUserName());
      }
    }
    return Optional.ofNullable(userJson);
  }


  protected List<String> getAnonymousAccessPaths() {
    return properties.getAnonymousAccessPaths();
  }

  /**
   * Exchange OAuth2 code for a (AD) JWT token.
   *
   * @param   code  Code for getting token from AD.
   * @return  Token from AD.
   */
  private String exchangeCodeForToken(String code) {
    String clientIdParam = createOAuth2Param(OAUTH2_CLIENT_ID_PARAM, properties.getOauth2ClientId());
    String redirectUriParam = createOAuth2Param(OAUTH2_REDIRECT_URI_PARAM, properties.getOauth2RedirectUri());
    String codeParam = createOAuth2Param(OAUTH2_CODE_PARAM, code);
    String tokenExchangeBody =
        new StringJoiner("&").add(clientIdParam).add(redirectUriParam).add(codeParam).add(OAUTH2_GRANT_TYPE).toString();

    TokenWrapper tokenWrapper = restTemplate.postForObject(properties.getOauth2TokenUrl(), tokenExchangeBody, TokenWrapper.class);
    logger.debug("AD token: {}", tokenWrapper.access_token);
    return tokenWrapper.access_token;
  }

  private String createOAuth2Param(String paramName, String paramValue) {
    return paramName + "=" + paramValue;
  }

  private AdJwtTokenFields parseToken(String token) {
    final Claims claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();

    List<String> groups = claims.get(AD_GROUP, List.class);
    boolean hasAlluGroup = groups == null ? false : groups.stream().filter(g -> g.equals(AD_ALLU_GROUP_NAME)).findFirst().isPresent();
    return new AdJwtTokenFields(
        claims.get(AD_USER_NAME, String.class),
        claims.get(AD_REAL_NAME, String.class),
        claims.get(AD_EMAIL, String.class),
        hasAlluGroup);
  }

  private static class AdJwtTokenFields {
    public final String winAccountName;
    public final String uniqueName;
    public final String email;
    public final boolean hasAlluGroup;

    public AdJwtTokenFields(String winAccountName, String uniqueName, String email, boolean hasAlluGroup) {
      this.winAccountName = winAccountName;
      this.uniqueName = uniqueName;
      this.email = email;
      this.hasAlluGroup = hasAlluGroup;
    }
  }

  /**
   * JSON mapping for AD JWT: {"access_token":"...","token_type":"bearer","expires_in":3600}
   */
  public static class TokenWrapper {
    public String access_token;
    public String token_type;
    public long expires_in;
  }

  protected UserService getUserService() {
    return userService;
  }

}
