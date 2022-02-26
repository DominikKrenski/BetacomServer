package org.dominik.server.services.definitions;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.dominik.server.data.User;

public interface ApiService {
  Future<JsonObject> findUserByLogin(String login);
  Future<String> save(User user);
}
