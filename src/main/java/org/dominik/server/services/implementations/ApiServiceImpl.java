package org.dominik.server.services.implementations;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.dominik.server.data.User;
import org.dominik.server.services.definitions.ApiService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    return client
      .save(
        USER_COLLECTION,
        new JsonObject()
          .put("login", user.getLogin())
          .put("password", passwordEncoder.encode(user.getPassword()))
      );
  }
}
