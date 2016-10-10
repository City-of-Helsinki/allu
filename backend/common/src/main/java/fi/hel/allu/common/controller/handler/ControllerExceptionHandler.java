package fi.hel.allu.common.controller.handler;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.exception.NonUniqueException;
import fi.hel.allu.common.exception.SearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@ControllerAdvice
public class ControllerExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

  @ExceptionHandler({IllegalArgumentException.class,  SearchException.class})
  public void handleBadRequests(RuntimeException e, HttpServletResponse response) throws IOException {
    logger.error(e.getMessage(), e);
    response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
  }

  @ExceptionHandler
  public void handleNotFound(NoSuchEntityException e, HttpServletResponse response) throws IOException {
    logger.error(e.getMessage(), e);
    response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
  }

  @ExceptionHandler
  public void handleNonUnique(NonUniqueException e, HttpServletResponse response) throws IOException {
    logger.error(e.getMessage(), e);
    response.sendError(HttpStatus.CONFLICT.value(), e.getMessage());
  }

  @ExceptionHandler
  public void handleIOException(IOException e, HttpServletResponse response) {
    logger.error(e.getMessage(), e);
    try {
      response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    } catch (IOException exc) {
      logger.error("Error while sending error response: {}", exc.getMessage());
    }
  }

}
