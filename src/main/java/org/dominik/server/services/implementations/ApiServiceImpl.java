package org.dominik.server.services.implementations;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.dominik.server.data.User;
import org.dominik.server.services.definitions.ApiService;

public final class ApiServiceImpl implements ApiService {
  private static final String USER_COLLECTION = "users";
  private static final String ITEM_COLLECTION = "items";

  private final MongoClient client;

  public ApiServiceImpl(MongoClient client) {
    this.client = client;
  }

  public Future<JsonObject> findUserByLogin(String login) {
    JsonObject query = new JsonObject().put("login", login);
    return client.findOne(USER_COLLECTION, query, new JsonObject().putNull("_id"));
  }

  public Future<String> save(User user) {
    return client
      .save(
        USER_COLLECTION,
        new JsonObject()
          .put("login", user.getLogin())
          .put("password", user.getPassword())
      );
  }
}
