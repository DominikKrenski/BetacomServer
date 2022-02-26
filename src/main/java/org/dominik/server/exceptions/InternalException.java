package org.dominik.server.exceptions;

public final class InternalException extends BaseException {

  public InternalException(String message) {
    super(message, 500);
  }
}
