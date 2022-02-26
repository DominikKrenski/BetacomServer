package org.dominik.server.data;

import java.util.UUID;

public final class User {
  private UUID id;
  private String login;
  private String password;

  public User() {}

  public User(UUID id, String login, String password) {
    this.id = id;
    this.login = login;
    this.password = password;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    return "User{" +
      "id=" + id +
      ", login='" + login + '\'' +
      ", password='" + password + '\'' +
      '}';
  }
}
