package org.dominik.server.exceptions;

public class UnauthorizedException extends BaseException{

  public UnauthorizedException(String message) {
    super(message, 401);
  }
}
