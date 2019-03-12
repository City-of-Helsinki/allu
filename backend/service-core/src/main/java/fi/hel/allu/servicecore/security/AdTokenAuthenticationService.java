package fi.hel.allu.servicecore.security;

import java.time.ZonedDateTime;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.security.domain.AdClaims;
import fi.hel.allu.servicecore.service.UserService;

/**
 * Base class for AD authentication services
 * @author User
 *
 */
public abstract class AdTokenAuthenticationService extends AuthenticationServiceInterface {

  @Autowired
  private AadService aadService;

  private static final Logger logger = LoggerFactory.getLogger(AdTokenAuthenticationService.class);

  private AdAuthenticationProperties properties;
  private UserService userService;
  private TokenUtil tokenUtil;

  protected AdTokenAuthenticationService(AdAuthenticationProperties properties, UserService userService, AadService aadService) {
    this.properties = properties;
    this.userService = userService;
    this.aadService = aadService;
    this.tokenUtil = new TokenUtil(properties.getJwtSecret());
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
    try {
      AdClaims adClaims = aadService.exchangeCodeForToken(code);
      return authenticateWithAdToken(adClaims);
    } catch (Exception e) {
      logger.error("AD authentication failed", e);
      return Optional.empty();
    }
  }

  public Optional<UserJson> authenticateWithAdToken(String accessToken) {
    try {
      AdClaims adClaims = aadService.parseToken(accessToken);
      return authenticateWithAdToken(adClaims);
    } catch (Exception ex) {
      logger.error("Invalid access token ", ex);
      return Optional.empty();
    }
  }

  protected Optional<UserJson> authenticateWithAdToken(AdClaims adClaims) {
    UserJson userJson = null;
    String username = StringUtils.lowerCase(adClaims.getUserName());
    try {
      userJson = userService.findUserByUserName(username);
    } catch (NoSuchEntityException e) {
      if (adClaims.isInGroup(properties.getAlluAdGroupId())) {
        userJson = new UserJson(
            null,
            username,
            adClaims.getRealName(),
            adClaims.getEmail(),
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

  protected UserService getUserService() {
    return userService;
  }

}
