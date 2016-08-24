package fi.hel.allu.ui.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

/**
 * Modified RestTemplate for calling GeoServer WFS interface, which returns confusing media type "text/xml; subtype=gml/2.1.2". The only
 * purpose of this modified RestTemplate is to change the media type so that Spring is happy with it.
 */
@Component
public class WfsRestTemplate extends RestTemplate {

  private MediaType defaultResponseContentType;

  public WfsRestTemplate() {
    super();
  }

  public WfsRestTemplate(ClientHttpRequestFactory requestFactory) {
    super(requestFactory);
  }

  public void setDefaultResponseContentType(String defaultResponseContentType) {
    this.defaultResponseContentType = MediaType.parseMediaType(defaultResponseContentType);
  }

  @Override
  protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback, final ResponseExtractor<T> responseExtractor)
      throws RestClientException {

    return super.doExecute(url, method, requestCallback, new ResponseExtractor<T>() {
      public T extractData(ClientHttpResponse response) throws IOException {
        response.getHeaders().remove(HttpHeaders.CONTENT_TYPE);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE + "; charset=utf-8");

        return responseExtractor.extractData(response);
      }
    });
  }
}
