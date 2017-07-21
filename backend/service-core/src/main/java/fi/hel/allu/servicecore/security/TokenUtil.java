package fi.hel.allu.servicecore.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TokenUtil {

  /**
   * Decode given JSON Web Token using application's secret key.
   *
   * @param secret        Secret key.
   * @param roleClaimName Name of the role claim.
   * @param token         JWT that is decoded.
   * @return <code>User</code> parsed from the token.
   */
  static public User parseUserFromToken(String secret, String roleClaimName, String token) {
    final Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    return new User(claims.getSubject(), "", getRoles(roleClaimName, claims));
  }

  static private Set<GrantedAuthority> getRoles(String roleClaimName, Claims claims) {
    List<String> roles = claims.get(roleClaimName, List.class);
    return roles.stream().map(r -> new SimpleGrantedAuthority(r)).collect(Collectors.toSet());
  }
}
