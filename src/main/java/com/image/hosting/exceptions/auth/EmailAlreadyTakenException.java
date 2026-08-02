package com.image.hosting.exceptions.auth;

public class EmailAlreadyTakenException extends RuntimeException {
  public EmailAlreadyTakenException(String message) {
    super(message);
  }
}
