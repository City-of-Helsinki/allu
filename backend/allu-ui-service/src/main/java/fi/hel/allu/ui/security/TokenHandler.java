package fi.hel.allu.ui.security;

import fi.hel.allu.ui.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TokenHandler {

    public static final String EMAIL = "emailAddress";
    public static final String ROLES = "alluRoles";
    private String secret;
    private int expirationHours;
    private UserService userService;


    public TokenHandler(String secret, int expirationHours, UserService userService) {
        this.secret = secret;
        this.expirationHours = expirationHours;
        this.userService = userService;
    }

    /**
     * Create a token using the given parameters and sign it using the application’s secret key.
     *
     * @param user User that contains principal that can be used to identify the user related to the JWT.
     * @return signed token
     */
    public String createTokenForUser(AlluUser user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().length() == 0) {
            throw new IllegalArgumentException("user principle name must not be null");
        }
        LocalDateTime dateTimeToConvert = LocalDateTime.now().plusHours(expirationHours);
        Date convertToDate = Date.from(dateTimeToConvert.atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setExpiration(convertToDate)
                .setSubject(user.getUsername())
                .claim(ROLES, user.getAuthorities())
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
        final Claims claims = Jwts.parser().setSigningKey(secret)
                .parseClaimsJws(token).getBody();
        return userService.loadUserByUsername(claims.getSubject());
    }

}
