package fi.hel.allu.servicecore.security;

import java.net.MalformedURLException;
import java.security.PublicKey;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwk.JwkException;

import fi.hel.allu.servicecore.security.domain.AdClaims;
import fi.hel.allu.servicecore.security.domain.TokenWrapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * Service to access Azure AD
 */
@Service
public class AadService {

  private static final String OAUTH2_CLIENT_ID_PARAM = "client_id";
  private static final String OAUTH2_CLIENT_SECRET_PARAM = "client_secret";
  private static final String OAUTH2_REDIRECT_URI_PARAM = "redirect_uri";
  private static final String OAUTH2_CODE_PARAM = "code";
  private static final String OAUTH2_GRANT_TYPE_PARAM = "grant_type";
  private static final String OAUTH2_GRANT_TYPE = "authorization_code";

  @Lazy
  @Autowired
  private AdAuthenticationProperties properties;
  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private JwksService jwksService;



  public AdClaims exchangeCodeForToken(String authCode) throws MalformedURLException, JwkException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
    map.add(OAUTH2_CLIENT_ID_PARAM, properties.getOauth2ClientId());
    map.add(OAUTH2_CLIENT_SECRET_PARAM, properties.getOauth2ClientSecret());
    map.add(OAUTH2_REDIRECT_URI_PARAM, properties.getOauth2RedirectUri());
    map.add(OAUTH2_CODE_PARAM, authCode);
    map.add(OAUTH2_GRANT_TYPE_PARAM, OAUTH2_GRANT_TYPE);

    TokenWrapper response = restTemplate.postForObject(properties.getOauth2TokenUrl(), request, TokenWrapper.class);
    return parseToken(response.getAccess_token());
  }

  public AdClaims parseToken(String token) throws MalformedURLException, JwkException {
    PublicKey publicKey = jwksService.getAdPublicKey(token);
    // Validates signature and expiration time
    final Claims claims = Jwts.parserBuilder().setSigningKey(publicKey).build()
      .parseClaimsJws(token).getBody();
    validateClaims(claims);
    return new AdClaims(claims);
  }

  private void validateClaims(Claims claims) {
    if (!Objects.equals(claims.getAudience(), properties.getOauth2ClientId())) {
      throw new IllegalArgumentException("Invalid audience in access token");
    }
  }
}
