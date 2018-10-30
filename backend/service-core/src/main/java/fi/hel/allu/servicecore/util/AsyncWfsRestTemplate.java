package fi.hel.allu.servicecore.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRequestCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;

import java.net.URI;

/**
 * Modified AsyncRestTemplate for calling GeoServer WFS interface, which returns confusing media type "text/xml; subtype=gml/2.1.2". The only
 * purpose of this modified RestTemplate is to change the media type so that Spring is happy with it.
 */
@Component
public class AsyncWfsRestTemplate extends AsyncRestTemplate {

  @Override
  protected <T extends Object> ListenableFuture<T> doExecute(
      URI url, HttpMethod method, AsyncRequestCallback requestCallback, ResponseExtractor<T> responseExtractor)
          throws RestClientException {

    return super.doExecute(url, method, requestCallback, (ClientHttpResponse response) -> {
      response.getHeaders().remove(HttpHeaders.CONTENT_TYPE);
      response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE + "; charset=utf-8");

      return responseExtractor.extractData(response);
    });
  }
}
