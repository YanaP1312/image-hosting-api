package com.image.hosting.exceptions;

import com.image.hosting.exceptions.auth.EmailAlreadyTakenException;
import com.image.hosting.exceptions.auth.InvalidCredentialException;
import com.image.hosting.exceptions.auth.InvalidSessionException;
import com.image.hosting.exceptions.image.ForbiddenImageAccessException;
import com.image.hosting.exceptions.image.ImageNotFoundException;
import com.image.hosting.exceptions.image.ImageTooLargeException;
import com.image.hosting.exceptions.image.UnsupportedImageFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, String> handleUserNotFound(UserNotFoundException ex) {
    return Map.of("error", ex.getMessage());
  }

  @ExceptionHandler(EmailAlreadyTakenException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public Map<String, String> handleEmailTaken(EmailAlreadyTakenException ex) {
    return Map.of("error", ex.getMessage());
  }

  @ExceptionHandler(InvalidCredentialException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public Map<String, String> handleInvalidCredential(InvalidCredentialException ex) {
    return Map.of("error", ex.getMessage());
  }

  @ExceptionHandler(InvalidSessionException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public Map<String, String> handleInvalidSession(InvalidSessionException ex) {
    return Map.of("error", ex.getMessage());
  }

  @ExceptionHandler(ForbiddenImageAccessException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Map<String, String> handleForbiddenAccess(ForbiddenImageAccessException ex) {
    return Map.of("error", ex.getMessage());
  }

  @ExceptionHandler(ImageNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, String> handleImageNotFound(ImageNotFoundException ex) {
    return Map.of("error", ex.getMessage());
  }

  @ExceptionHandler(ImageTooLargeException.class)
  @ResponseStatus(HttpStatus.CONTENT_TOO_LARGE)
  public Map<String, String> handleImageTooLarge(ImageTooLargeException ex) {
    return Map.of("error", ex.getMessage());
  }

  @ExceptionHandler(UnsupportedImageFormatException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleUnsupportedFormat(UnsupportedImageFormatException ex) {
    return Map.of("error", ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField().replaceAll("([A-Z])", "_$1").toLowerCase(), error.getDefaultMessage())
    );
    return errors;
  }


}
