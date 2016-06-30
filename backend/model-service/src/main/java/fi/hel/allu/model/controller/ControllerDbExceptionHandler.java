package fi.hel.allu.model.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.querydsl.core.QueryException;

/**
 * Handler for database-originated exceptions
 */
@ControllerAdvice
public class ControllerDbExceptionHandler {

  @ExceptionHandler({ DataIntegrityViolationException.class, QueryException.class })
  void handleBadRequests(RuntimeException e, HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
  }
}
