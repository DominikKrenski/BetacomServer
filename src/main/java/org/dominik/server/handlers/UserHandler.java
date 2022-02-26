package org.dominik.server.handlers;

import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.dominik.server.data.User;
import org.dominik.server.exceptions.ConflictException;
import org.dominik.server.services.definitions.ApiService;


public final class UserHandler {
  private final ApiService apiService;

  public UserHandler(ApiService apiService) {
    this.apiService = apiService;
  }

  public void register(RoutingContext ctx) {
    var data = Json.decodeValue(ctx.getBodyAsString(), User.class);

    apiService
      .findUserByLogin(data.getLogin())
      .compose(res -> {
        if (res == null) {
          return apiService.save(data);
        } else {
          throw new ConflictException("User already exists");
        }
      })
      .onSuccess(res -> ctx.response().setStatusCode(201).end())
      .onFailure(ctx::fail);
  }

}
