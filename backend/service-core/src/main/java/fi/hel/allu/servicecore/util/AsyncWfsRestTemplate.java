package fi.hel.allu.servicecore.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * Modified RestTemplate for calling GeoServer WFS interface, which returns confusing media type "text/xml; subtype=gml/2.1.2". The only
 * purpose of this modified RestTemplate is to change the media type so that Spring is happy with it.
 *
 * <p>Previously extended the now-removed {@code AsyncRestTemplate}. Calls are issued
 * synchronously here; callers that need fan-out/fan-in concurrency wrap individual
 * {@code exchange(...)} calls in {@link java.util.concurrent.CompletableFuture#supplyAsync}
 * with an explicit {@link java.util.concurrent.Executor} (see
 * {@code AbstractWfsPaymentDataService}).</p>
 */
@Component
public class AsyncWfsRestTemplate extends RestTemplate {

  @Override
  protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor)
      throws RestClientException {

    return super.doExecute(url, method, requestCallback, (ClientHttpResponse response) -> {
      response.getHeaders().remove(HttpHeaders.CONTENT_TYPE);
      response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE + "; charset=utf-8");

      return responseExtractor.extractData(response);
    });
  }
}
