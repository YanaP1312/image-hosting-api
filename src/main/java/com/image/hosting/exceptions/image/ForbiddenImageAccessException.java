package com.image.hosting.exceptions.image;

public class ForbiddenImageAccessException extends RuntimeException {
  public ForbiddenImageAccessException(String message) {
    super(message);
  }
}
