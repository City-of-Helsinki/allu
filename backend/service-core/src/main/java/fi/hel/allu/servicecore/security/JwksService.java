package fi.hel.allu.servicecore.security;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.PublicKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;

@Service
public class JwksService {

  @Lazy
  @Autowired
  private AdAuthenticationProperties properties;

  public PublicKey getAdPublicKey(String accessToken) throws MalformedURLException, JwkException {
    JwkProvider provider = new UrlJwkProvider(new URL(properties.getOauth2JwksUri()));
    String kid = JwtHelper.headers(accessToken).get("kid");
    Jwk jwk = provider.get(kid);
    return jwk.getPublicKey();
  }
}
