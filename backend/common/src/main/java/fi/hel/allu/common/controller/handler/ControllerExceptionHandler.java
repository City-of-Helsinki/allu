package fi.hel.allu.common.controller.handler;

import fi.hel.allu.common.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);
  private final MessageSourceAccessor validationMessageAccessor;
  private final MessageSourceAccessor errorMessageAccessor;
  private final ControllerExceptionHandlerConfig config;

  @Autowired
  public ControllerExceptionHandler(
      MessageSource errorMessageSource,
      MessageSource validationMessageSource,
      ControllerExceptionHandlerConfig config) {
    this.validationMessageAccessor = new MessageSourceAccessor(validationMessageSource);
    this.errorMessageAccessor = new MessageSourceAccessor(errorMessageSource);
    this.config = config;
  }

  @ExceptionHandler({IllegalArgumentException.class,  SearchException.class})
  protected ResponseEntity<Object> handleBadRequests(RuntimeException e, WebRequest request) {
    logger.warn("Request is bad: {}", HttpStatus.BAD_REQUEST);
    logger.error(e.getMessage(), e);
    return handleExceptionInternal(e, getErrorBody(e), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(value = { NoSuchEntityException.class })
  protected ResponseEntity<Object> handleNotFound(RuntimeException e, WebRequest request) {
    logger.warn("Not Found: {}", HttpStatus.NOT_FOUND);
    logger.error(e.getMessage(), e);
    return handleExceptionInternal(e, getErrorBody(e), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler({NonUniqueException.class})
  protected ResponseEntity<Object> handleNonUnique(RuntimeException e, WebRequest request) {
    logger.warn("NonUnique: {}", HttpStatus.CONFLICT);
    logger.error(e.getMessage(), e);
    return handleExceptionInternal(e, getErrorBody(e), new HttpHeaders(), HttpStatus.CONFLICT, request);
  }

  @ExceptionHandler({NotImplementedException.class})
  protected ResponseEntity<Object> handleNotImplemented(RuntimeException e, WebRequest request) {
    logger.warn("Not implemented: {}", HttpStatus.NOT_IMPLEMENTED);
    logger.error(e.getMessage(), e);
    return handleExceptionInternal(e, getErrorBody(e), new HttpHeaders(), HttpStatus.NOT_IMPLEMENTED, request);
  }

  @ExceptionHandler({IOException.class})
  protected ResponseEntity<Object> handleServerErrorException(Exception e, WebRequest request) {
    logger.warn("Server Error: {}", HttpStatus.INTERNAL_SERVER_ERROR);
    logger.error(e.getMessage(), e);
    return handleExceptionInternal(e, getErrorBody(e), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  @ExceptionHandler({IllegalOperationException.class})
  protected ResponseEntity<Object> handleIllegalOperation(RuntimeException e, WebRequest request) {
    logger.warn("Forbidden: {}", HttpStatus.FORBIDDEN);
    logger.error(e.getMessage(), e);
    return handleExceptionInternal(e, getErrorBody(e), new HttpHeaders(), HttpStatus.FORBIDDEN, request);
  }

  @ExceptionHandler({MailSendException.class})
  protected ResponseEntity<Object> handleMailSendException(RuntimeException e, WebRequest request) {
    logger.warn("Mail send: {}",  HttpStatus.INTERNAL_SERVER_ERROR);
    logger.error(e.getMessage(), e);
    return handleExceptionInternal(e, getErrorBody(e), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  @ExceptionHandler({OptimisticLockException.class})
  protected ResponseEntity<Object> handleOptimisticLockException(RuntimeException e, WebRequest request) {
    logger.warn("OptimisticLock: {}",  HttpStatus.CONFLICT);
    logger.error(e.getMessage(), e);
    return handleExceptionInternal(e, getErrorBody(e), new HttpHeaders(), HttpStatus.CONFLICT, request);
  }

  @Override
  protected ResponseEntity<Object>
  handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                               HttpHeaders headers, HttpStatus status,
                               WebRequest request) {
    BindingResult bindingResult = e.getBindingResult();
    List<ErrorInfo> errorInfoList = new ArrayList<>();
    logger.warn("MethodArgument not valid: {}, {}", HttpStatus.BAD_REQUEST, status);
    logger.error("MethodArgument: {}", e);
    bindingResult.getGlobalErrors().stream().map((error) -> {
      ErrorInfo errorInfo = new ErrorInfo();
      errorInfo.setErrorMessage(error.getDefaultMessage());
      return errorInfo;
    }).forEachOrdered((errorInfo) -> errorInfoList.add(errorInfo));

    bindingResult.getFieldErrors().stream().map((error) -> {
      ErrorInfo errorInfo = new ErrorInfo();
      errorInfo.setErrorMessage(getValidationMessage(error.getDefaultMessage()));
      errorInfo.setAdditionalInfo(error.getField());
      return errorInfo;
    }).forEachOrdered((errorInfo) -> errorInfoList.add(errorInfo));

    return handleExceptionInternal(e, errorInfoList, headers, HttpStatus.BAD_REQUEST, request);
  }

  private Object getErrorBody(Exception e) {
    if (config.isTranslateErrorMessages()) {
      final List<ErrorInfo> errorInfoList = new ArrayList<>();
      try {
        final String message = errorMessageAccessor.getMessage(e.getMessage());
        ErrorInfo info = new ErrorInfo();
        info.setErrorMessage(message);
        Optional.ofNullable(e.getCause())
          .ifPresent(throwable ->
            info.setAdditionalInfo(getAdditionalInfoMessage(e.getCause())));
        errorInfoList.add(info);
        return errorInfoList;
      } catch (NoSuchMessageException ne) {
        logger.warn("Didn't find the message for key {}", e.getMessage());
        return null;
      }
    }
    return e;
  }

  private String getAdditionalInfoMessage(Throwable t) {
    if (t instanceof ExceptionWithExtraInfo) {
      return errorMessageAccessor.getMessage(t.getMessage(), new String[] { ((ExceptionWithExtraInfo) t).getExtraInfo() });
    } else {
      return errorMessageAccessor.getMessage(t.getMessage());
    }
  }

  private String getValidationMessage(String key) {
    if (!config.isTranslateValidationMessages()) {
      return key;
    }

    try {
      final String message = validationMessageAccessor.getMessage(key);
      return message;
    } catch (NoSuchMessageException ne) {
      logger.warn("Didn't find the message for key {}", key);
      return key;
    }
  }
}