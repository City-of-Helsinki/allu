package fi.hel.allu.model.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.querydsl.core.QueryException;

import fi.hel.allu.NoSuchEntityException;

@ControllerAdvice
public class ControllerExceptionHandler {

  @ExceptionHandler({ IllegalArgumentException.class, QueryException.class })
  void handleBadRequests(RuntimeException e, HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
  }

  @ExceptionHandler
  void handleNotFound(NoSuchEntityException e, HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
  }
}
