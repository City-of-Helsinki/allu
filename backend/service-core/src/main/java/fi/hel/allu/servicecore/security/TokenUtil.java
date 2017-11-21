package fi.hel.allu.servicecore.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TokenUtil {

  /** JWT property used for Allu external service public interface roles */
  public static final String PROPERTY_ROLE_ALLU_PUBLIC = "publicAlluRoles";
  /** JWT property property used for Allu UI roles */
  public static final String PROPERTY_ROLE_ALLU = "alluRoles";
  /** JWT property for emails */
  public static final String PROPERTY_EMAIL = "emailAddress";

  private final String secret;

  public TokenUtil(String secret) {
    this.secret = secret;
  }

  /**
   * Create JWT as a string.
   *
   * @param expirationTime  The time JWT expires.
   * @param subject         Subject of the JWT (the username for example).
   * @param propertyToValue Map consisting of JWT properties to be set. Map key is property name and value is value of the property. Value
   *                        may be a single string or list of strings.
   * @return  JWT as a string.
   */
  public String createToken(ZonedDateTime expirationTime, String subject, Map<String, Object> propertyToValue) {
    Date convertToDate = Date.from(expirationTime.toInstant());
    JwtBuilder jwtBuilder = Jwts.builder()
        .setExpiration(convertToDate)
        .setSubject(subject);
    propertyToValue.entrySet().forEach(e -> jwtBuilder.claim(e.getKey(), e.getValue()));
    return jwtBuilder.signWith(SignatureAlgorithm.HS512, getBase64EncodedJWTSecret()).compact();
  }

  /**
   * Decode given JSON Web Token using application's secret key.
   *
   * @param roleClaimName Name of the role claim.
   * @param token         JWT that is decoded.
   * @return <code>User</code> parsed from the token.
   */
  public User parseUserFromToken(String roleClaimName, String token) {
    final Claims claims = Jwts.parser().setSigningKey(getBase64EncodedJWTSecret()).parseClaimsJws(token).getBody();
    return new User(claims.getSubject(), "", getRoles(roleClaimName, claims));
  }

  private Set<GrantedAuthority> getRoles(String roleClaimName, Claims claims) {
    Optional<List<String>> rolesOpt = Optional.ofNullable(claims.get(roleClaimName, List.class));
    return rolesOpt.orElse(Collections.emptyList()).stream().map(r -> new SimpleGrantedAuthority(r)).collect(Collectors.toSet());
  }

  String getBase64EncodedJWTSecret() {
    try {
      return new String(java.util.Base64.getEncoder().encode(secret.getBytes()), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      // should never happen
      throw new RuntimeException(e);
    }
  }
}
