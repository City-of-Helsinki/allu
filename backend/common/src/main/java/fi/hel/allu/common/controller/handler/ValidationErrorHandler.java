package fi.hel.allu.common.controller.handler;


import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import fi.hel.allu.common.exception.ErrorInfo;

@ControllerAdvice
public class ValidationErrorHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object>
  handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                               HttpHeaders headers, HttpStatus status,
                               WebRequest request) {
    BindingResult bindingResult = e.getBindingResult();
    List<ErrorInfo> errorInfoList = new ArrayList<>();

    for (ObjectError error : bindingResult.getGlobalErrors()) {
      ErrorInfo errorInfo = new ErrorInfo();
      errorInfo.setErrorMessage(error.getDefaultMessage());
      errorInfoList.add(errorInfo);
    }
    for (FieldError error : bindingResult.getFieldErrors()) {
      ErrorInfo errorInfo = new ErrorInfo();
      errorInfo.setErrorMessage(error.getDefaultMessage());
      errorInfo.setAdditionalInfo(error.getField());
      errorInfoList.add(errorInfo);
    }
    return handleExceptionInternal(e, errorInfoList, headers, HttpStatus.BAD_REQUEST, request);
  }
}