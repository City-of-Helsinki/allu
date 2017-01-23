package fi.hel.allu.ui.security;

import fi.hel.allu.ui.domain.UserJson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TokenHandler {

  public static final String EMAIL = "emailAddress";
  public static final String ROLES = "alluRoles";

  private String secret;
  private Integer expirationHours;

  @Autowired
  TokenHandler(
          @Value("${jwt.secret}") String secret,
          @Value("${jwt.expiration.hours:12}") int expirationHours) {
    this.secret = secret;
    this.expirationHours = expirationHours;
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
    LocalDateTime dateTimeToConvert = LocalDateTime.now().plusHours(expirationHours);
    Date convertToDate = Date.from(dateTimeToConvert.atZone(ZoneId.systemDefault()).toInstant());

    return Jwts.builder()
        .setExpiration(convertToDate)
        .setSubject(user.getUserName())
        .claim(ROLES, user.getAssignedRoles())
        .claim(EMAIL, user.getEmailAddress())
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
  }

  /**
   * Decode given JSON Web Token using application's secret key and get user's details from the userService.
   *
   * @param token JWT that is decoded
   * @return <code>AlluUser</code> parsed from the token.
   */
  public User parseUserFromToken(String token) {
    final Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    String email = Optional.ofNullable(claims.get(EMAIL)).map(e -> e.toString()).orElse(null);
    return new AlluUser(claims.getSubject(), getRoles(claims), email);
  }

  private Set<GrantedAuthority> getRoles(Claims claims) {
    List<String> roles = claims.get(ROLES, List.class);
    return roles.stream().map(r -> new SimpleGrantedAuthority(r)).collect(Collectors.toSet());
  }
}
