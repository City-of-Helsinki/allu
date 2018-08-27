package fi.hel.allu.external.api;

import java.util.Collections;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import fi.hel.allu.external.domain.LoginExt;

/**
 * Base class for all external API tests
 */
@TestPropertySource(locations="classpath:test.properties")
public class BaseExternalApiTest {

  private static final String LOGIN_PATH = "/login";
  @Value("${ext.service.baseurl}")
  private String baseUrl;
  @Value("${service.password}")
  private String password;
  @Value("${service.user}")
  private String username;

  private String bearerToken = null;

  protected RestTemplate restTemplate = new RestTemplate();

  protected <T> HttpEntity<T> httpEntityWithHeaders() {
    return new HttpEntity<>(createAuthenticationHeader());
  }

  protected <T> HttpEntity<T> httpEntityWithHeaders(T requestBody) {
    return new HttpEntity<>(requestBody, createAuthenticationHeader());
  }


  private HttpHeaders createAuthenticationHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    setAuthorization(headers);
    return headers;
  }

  protected void setAuthorization(HttpHeaders headers) {
    if (bearerToken == null) {
      login();
    }
    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
  }

  protected String getExtServiceUrl(String resourcePath, MultiValueMap<String, String> queryParams) {
    return UriComponentsBuilder.fromHttpUrl(getExtServiceUrl(resourcePath))
      .queryParams(queryParams)
      .buildAndExpand().toUri().toString();
  }

  protected String getExtServiceUrl(String resourcePath) {
    return baseUrl + resourcePath;
  }

  protected <T> MultiValueMap<String, T> requestParam(String name, T value) {
    MultiValueMap<String, T> params = new LinkedMultiValueMap<>();
    params.put(name, Collections.singletonList(value));
    return params;
  }

  private void login() {
    ResponseEntity<String> response = restTemplate.exchange(
        getExtServiceUrl(LOGIN_PATH),
        HttpMethod.POST,
        new HttpEntity<>(new LoginExt(username, password)),
        String.class);
    this.bearerToken = response.getBody();
  }





}
