package fi.hel.allu.servicecore.security;

import com.greghaskins.spectrum.Spectrum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.junit.runner.RunWith;
import org.springframework.security.core.userdetails.User;

import java.time.ZonedDateTime;
import java.util.*;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Spectrum.class)
public class TokenUtilSpec {

  TokenUtil tokenUtil;

  String secret = "test secret";
  ZonedDateTime expirationTime = ZonedDateTime.now().plusHours(1);
  String subject = "testuser";
  String simpleClaim = "simpleClaim";
  String simpleClaimValue = "simpleClaimValue";
  String arrayClaim = "arrayClaim";
  List<String> arrayClaimValue = Arrays.asList("value1", "value2");
  String role = "testrole";

  String token;

  {
    describe("Creating JWT", () -> {
      beforeEach(() -> {
        tokenUtil = new TokenUtil(secret);
        Map<String, Object> claims = new HashMap<>();
        claims.put(simpleClaim, simpleClaimValue);
        claims.put(arrayClaim, arrayClaimValue);
        claims.put(TokenUtil.PROPERTY_ROLE_ALLU, Collections.singletonList(role));
        token = tokenUtil.createToken(expirationTime, subject, claims);
      });
      it("should be signed", () -> {
        JwtParser parser = Jwts.parser().setSigningKey(tokenUtil.getBase64EncodedJWTSecret());
        assertTrue("token must be signed", parser.isSigned(token));
      });
      it("should have subject", () -> {
        User user = tokenUtil.parseUserFromToken(TokenUtil.PROPERTY_ROLE_ALLU, token);
        assertEquals(subject, user.getUsername());
      });
      it("should have roles", () -> {
        User user = tokenUtil.parseUserFromToken(TokenUtil.PROPERTY_ROLE_ALLU, token);
        assertEquals(1, user.getAuthorities().size());
        assertEquals(role, user.getAuthorities().stream().findFirst().get().getAuthority());
      });
      it("should have all claims", () -> {
        final Claims claims = Jwts.parser().setSigningKey(tokenUtil.getBase64EncodedJWTSecret()).parseClaimsJws(token).getBody();
        assertEquals(5, claims.size());
        // milliseconds seem to disappear from JWT
        assertEquals(Date.from(expirationTime.toInstant()).getTime() / 1000, claims.getExpiration().getTime() / 1000);
        assertEquals(subject, claims.getSubject());
        assertEquals(Arrays.asList(role), claims.get(TokenUtil.PROPERTY_ROLE_ALLU));
        assertEquals(simpleClaimValue, claims.get(simpleClaim));
        assertEquals(arrayClaimValue, claims.get(arrayClaim));
      });
    });
  }
}
