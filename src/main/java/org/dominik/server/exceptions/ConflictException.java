package org.dominik.server.exceptions;

public final class ConflictException extends BaseException {

  public ConflictException(String message) {
    super(message, 409);
  }
}
