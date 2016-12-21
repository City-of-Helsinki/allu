package fi.hel.allu.model.controller;

import com.querydsl.core.QueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handler for database-originated exceptions
 */
@ControllerAdvice
public class ControllerDbExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(ControllerDbExceptionHandler.class);


  @ExceptionHandler({ DataIntegrityViolationException.class, QueryException.class })
  void handleBadRequests(RuntimeException e, HttpServletResponse response) throws IOException {
    logger.error("Data integrity violation", e);
    response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
  }
}
