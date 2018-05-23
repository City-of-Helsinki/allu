package fi.hel.allu.common.controller.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import fi.hel.allu.common.exception.NoSuchEntityException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.stream.Collectors;

public class ServiceResponseErrorHandler implements ResponseErrorHandler {
  private static final Logger logger = LoggerFactory.getLogger(ServiceResponseErrorHandler.class);
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
    if (!clientHttpResponse.getStatusCode().is2xxSuccessful()) {
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
      throw new NoSuchEntityException(getMessage(clientHttpResponse));
    } else if (clientHttpResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
      logger.error("{} response. Throwing IllegalArgumentException", HttpStatus.BAD_REQUEST);
      throw new IllegalArgumentException(getMessage(clientHttpResponse));
    } else {
      logger.error("Not mapped error response. Throwing runtime exception. {} {}", clientHttpResponse.getStatusCode(),
          clientHttpResponse.getStatusText());
      throw new RuntimeException(getMessage(clientHttpResponse));
    }
  }

  private String getMessage(ClientHttpResponse clientHttpResponse) throws IOException {
    String result = new BufferedReader(new InputStreamReader(clientHttpResponse.getBody()))
        .lines().collect(Collectors.joining("\n"));
    JsonNode json = mapper.readTree(result);
    return json.path("message").asText();
  }
}
