package edu.miu.waa.onlineauctionapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ControllerExceptionHandler {

  @ExceptionHandler(value = {RecordNotFoundException.class})
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  public ErrorResponse resourceNotFoundException(RecordNotFoundException ex, WebRequest request) {
    ErrorResponse message =
        ErrorResponse.builder(ex, HttpStatus.NOT_FOUND, ex.getMessage()).build();
    return message;
  }

  @ExceptionHandler(value = {RecordAlreadyExistsException.class, InvalidInputException.class})
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  public ErrorResponse clientInputException(Exception ex, WebRequest request) {
    ErrorResponse message =
        ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage()).build();
    return message;
  }

  @ExceptionHandler(value = {HttpMessageNotReadableException.class})
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  public ErrorResponse jsonParseException(Exception ex, WebRequest request) {
    ErrorResponse message =
        ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, "Invalid value from client!").build();
    return message;
  }
}
