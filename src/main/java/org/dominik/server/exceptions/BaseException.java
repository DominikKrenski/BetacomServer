package org.dominik.server.exceptions;

import java.time.Instant;

public abstract class BaseException extends RuntimeException {
  private final int statusCode;
  private final Instant timestamp;

  public BaseException(String message, int statusCode) {
    super(message);
    this.timestamp = Instant.now();
    this.statusCode = statusCode;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public int getStatusCode() {
    return statusCode;
  }
}
