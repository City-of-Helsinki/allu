package fi.hel.allu.servicecore.security;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.PublicKey;
import java.text.ParseException;

@Service
public class JwksService {

    @Lazy
    @Autowired
    private AdAuthenticationProperties properties;


    public PublicKey getAdPublicKey(String accessToken) throws MalformedURLException, JwkException {
        JwkProvider provider = new UrlJwkProvider(new URL(properties.getOauth2JwksUri()));
        SignedJWT signedJWT;
        try {
            signedJWT = SignedJWT.parse(accessToken);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String kid = signedJWT.getHeader().getKeyID();
        Jwk jwk = provider.get(kid);
        return jwk.getPublicKey();
    }
}
