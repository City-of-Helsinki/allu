package fi.hel.allu.external.api;

import java.util.Collections;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Base class for all external API tests
 */
@TestPropertySource(locations="classpath:test.properties")
public class BaseExternalApiTest {

  @Value("${ext.service.baseurl}")
  private String baseUrl;
  @Value("${service.token}")
  private String bearerToken;

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





}
