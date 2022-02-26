package org.dominik.server.services.implementations;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.dominik.server.data.Item;
import org.dominik.server.data.User;
import org.dominik.server.services.definitions.ApiService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

public final class ApiServiceImpl implements ApiService {
  private static final String USER_COLLECTION = "users";
  private static final String ITEM_COLLECTION = "items";

  private final MongoClient client;

  public ApiServiceImpl(MongoClient client) {
    this.client = client;
  }

  @Override
  public Future<JsonObject> findUserByLogin(String login) {
    JsonObject query = new JsonObject().put("login", login);
    return client.findOne(USER_COLLECTION, query, null);
  }

  @Override
  public Future<String> save(User user) {
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    return client
      .save(
        USER_COLLECTION,
        new JsonObject()
          .put("_id", UUID.randomUUID().toString())
          .put("login", user.getLogin())
          .put("password", passwordEncoder.encode(user.getPassword()))
      );
  }

  @Override
  public Future<String> save(Item item, String ownerId) {
    return client
      .save(
        ITEM_COLLECTION,
        new JsonObject()
          .put("_id", UUID.randomUUID().toString())
          .put("owner", ownerId)
          .put("name", item.getName())
      );
  }

  @Override
  public Future<List<JsonObject>> findAllUserItems(String ownerId) {
    return client.find(ITEM_COLLECTION, new JsonObject().put("owner", ownerId));
  }
}
