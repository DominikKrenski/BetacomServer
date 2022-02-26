package org.dominik.server.exceptions;

public final class ForbiddenException extends BaseException {

  public ForbiddenException(String message) {
    super(message, 403);
  }
}
