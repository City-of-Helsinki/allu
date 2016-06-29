package fi.hel.allu.common.controller.handler;

import fi.hel.allu.common.exception.NoSuchEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class ServiceResponseErrorHandler implements ResponseErrorHandler {
  private static final Logger logger = LoggerFactory.getLogger(ServiceResponseErrorHandler.class);


  @Override
  public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
    if (clientHttpResponse.getStatusCode() != HttpStatus.OK) {
      logger.debug("Status code: {}", clientHttpResponse.getStatusCode());
      logger.debug("Status text: {}", clientHttpResponse.getStatusText());
      return true;
    }
    return false;
  }

  @Override
  public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
    if (clientHttpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
      logger.error("{} response. Throwing not such entity exception", HttpStatus.NOT_FOUND);
      throw new NoSuchEntityException(clientHttpResponse.getStatusText());
    } else if (clientHttpResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
      logger.error("{} response. Throwing IllegalArgumentException", HttpStatus.BAD_REQUEST);
      throw new IllegalArgumentException(clientHttpResponse.getStatusText());
    } else {
      logger.error("Not mapped error response. Throwing runtime exception. {} {}", clientHttpResponse.getStatusCode(),
          clientHttpResponse.getStatusText());
      throw new RuntimeException("Internal Error");
    }
  }
}
