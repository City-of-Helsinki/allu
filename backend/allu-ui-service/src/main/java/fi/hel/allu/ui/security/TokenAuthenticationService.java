package fi.hel.allu.ui.security;

import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.security.TokenUtil;
import fi.hel.allu.servicecore.security.UserAuthentication;
import fi.hel.allu.servicecore.service.AuthenticationServiceInterface;
import fi.hel.allu.servicecore.service.UserService;
import fi.hel.allu.ui.config.ApplicationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class TokenAuthenticationService extends AuthenticationServiceInterface {
  private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationService.class);

  private static final String OAUTH2_CLIENT_ID_PARAM = "client_id";
  private static final String OAUTH2_REDIRECT_URI_PARAM = "redirect_uri";
  private static final String OAUTH2_CODE_PARAM = "code";
  private static final String OAUTH2_GRANT_TYPE= "grant_type=authorization_code";
  // ADFS Token fields
  private static final String ADFS_USER_NAME = "winaccountname";
  private static final String ADFS_REAL_NAME = "unique_name";
  private static final String ADFS_EMAIL = "email";
  private static final String ADFS_GROUP = "group";
  private static final String ADFS_ALLU_GROUP_NAME = "sg_HKR_Allu";

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private UserService userService;

  private PublicKey publicKey;
  private TokenUtil tokenUtil;

  @Autowired
  public TokenAuthenticationService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      UserService userService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.userService = userService;
    this.tokenUtil = new TokenUtil(applicationProperties.getJwtSecret());
  }

  @PostConstruct
  void initializePublicKey() {
    try {
      CertificateFactory f = CertificateFactory.getInstance("X.509");
      X509Certificate certificate = (X509Certificate) f.generateCertificate(
          new ByteArrayInputStream(Base64.decode(applicationProperties.getOauth2Certificate().getBytes())));
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

  @Override
  public boolean isAnonymousAccessAllowedForPath(String path) {
    return applicationProperties.getAnonymousAccessPaths().contains(path);
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

    ZonedDateTime dateTimeToConvert = ZonedDateTime.now().plusHours(applicationProperties.getJwtExpirationHours());
    Map<String, Object> propertyNameToValue = new HashMap<>();
    propertyNameToValue.put(TokenUtil.PROPERTY_ROLE_ALLU, user.getAssignedRoles());
    propertyNameToValue.put(TokenUtil.PROPERTY_EMAIL, user.getEmailAddress());
    return tokenUtil.createToken(dateTimeToConvert, user.getUserName(), propertyNameToValue);
  }

  /**
   * Authenticates user with OAuth2 code against ADFS. If user does not exist in Allu user database and user belongs to allu group in ADFS,
   * user is automatically added to Allu user database with viewing rights.
   *
   * @param   code  OAuth2 authorization code grant code.
   * @return  User data if login using code was successful. Otherwise nothing.
   */
  public Optional<UserJson> authenticateWithOAuth2Code(String code) {
    String adfsToken = exchangeCodeForToken(code);
    AdfsJwtTokenFields tokenFields = parseToken(adfsToken);

    UserJson userJson = null;
    try {
      userJson = userService.findUserByUserName(tokenFields.winAccountName);
    } catch (NoSuchEntityException e) {
      if (tokenFields.hasAlluGroup) {
        userJson = new UserJson(
            null,
            tokenFields.winAccountName,
            tokenFields.uniqueName,
            tokenFields.email,
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

  /**
   * Exchange OAuth2 code for a (ADFS) JWT token.
   *
   * @param   code  Code for getting token from ADFS.
   * @return  Token from ADFS.
   */
  private String exchangeCodeForToken(String code) {
    String clientIdParam = createOAuth2Param(OAUTH2_CLIENT_ID_PARAM, applicationProperties.getOauth2ClientId());
    String redirectUriParam = createOAuth2Param(OAUTH2_REDIRECT_URI_PARAM, applicationProperties.getOauth2RedirectUri());
    String codeParam = createOAuth2Param(OAUTH2_CODE_PARAM, code);
    String tokenExchangeBody =
        new StringJoiner("&").add(clientIdParam).add(redirectUriParam).add(codeParam).add(OAUTH2_GRANT_TYPE).toString();

    TokenWrapper tokenWrapper = restTemplate.postForObject(applicationProperties.getOauth2TokenUrl(), tokenExchangeBody, TokenWrapper.class);
    logger.debug("ADFS token: {}", tokenWrapper.access_token);
    return tokenWrapper.access_token;
  }

  private AdfsJwtTokenFields parseToken(String token) {
    final Claims claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();

    List<String> groups = claims.get(ADFS_GROUP, List.class);
    boolean hasAlluGroup = groups == null ? false : groups.stream().filter(g -> g.equals(ADFS_ALLU_GROUP_NAME)).findFirst().isPresent();
    return new AdfsJwtTokenFields(
        claims.get(ADFS_USER_NAME, String.class),
        claims.get(ADFS_REAL_NAME, String.class),
        claims.get(ADFS_EMAIL, String.class),
        hasAlluGroup);
  }
  private static class AdfsJwtTokenFields {
    public final String winAccountName;
    public final String uniqueName;
    public final String email;
    public final boolean hasAlluGroup;

    public AdfsJwtTokenFields(String winAccountName, String uniqueName, String email, boolean hasAlluGroup) {
      this.winAccountName = winAccountName;
      this.uniqueName = uniqueName;
      this.email = email;
      this.hasAlluGroup = hasAlluGroup;
    }
  }

  private String createOAuth2Param(String paramName, String paramValue) {
    return paramName + "=" + paramValue;
  }

  /**
   * JSON mapping for ADFS JWT: {"access_token":"...","token_type":"bearer","expires_in":3600}
   */
  static class TokenWrapper {
    public String access_token;
    public String token_type;
    public long expires_in;
  }
}
