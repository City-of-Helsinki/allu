package fi.hel.allu.common.controller.handler;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import fi.hel.allu.common.exception.NoSuchEntityException;


@ControllerAdvice
public class ControllerExceptionHandler {

  @ExceptionHandler({IllegalArgumentException.class})
  public void handleBadRequests(RuntimeException e, HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
  }

  @ExceptionHandler
  public void handleNotFound(NoSuchEntityException e, HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
  }

}
