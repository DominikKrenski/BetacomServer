package org.dominik.server.services.definitions;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.dominik.server.data.Item;
import org.dominik.server.data.User;

import java.util.List;

public interface ApiService {
  Future<JsonObject> findUserByLogin(String login);
  Future<String> save(User user);
  Future<String> save(Item item, String ownerId);
  Future<List<JsonObject>> findAllUserItems(String ownerId);
}
