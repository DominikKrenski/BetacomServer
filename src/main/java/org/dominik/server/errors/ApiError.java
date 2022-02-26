package org.dominik.server.errors;

import java.time.Instant;

public final class ApiError {
  private final int status;
  private final Instant timestamp;
  private final String message;

  public ApiError(int status, Instant timestamp, String message) {
    this.status = status;
    this.timestamp = timestamp;
    this.message = message;
  }

  public int getStatus() {
    return status;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public String getMessage() {
    return message;
  }
}
