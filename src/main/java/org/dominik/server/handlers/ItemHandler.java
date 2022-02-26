package org.dominik.server.handlers;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;
import org.dominik.server.data.Item;
import org.dominik.server.exceptions.UnauthorizedException;
import org.dominik.server.services.definitions.ApiService;

import java.util.UUID;

public final class ItemHandler {
  private static final String AUTH_HEADER = "Authorization";
  private static final String SCHEME = "Bearer ";

  private final ApiService apiService;
  private final JWTAuth jwtProvider;

  public ItemHandler(ApiService apiService, JWTAuth jwtProvide) {
    this.apiService = apiService;
    this.jwtProvider = jwtProvide;
  }

  public void save(RoutingContext ctx) {
    String header = ctx.request().getHeader(AUTH_HEADER);

    if (header == null || !header.startsWith(SCHEME)) {
      ctx.fail(new UnauthorizedException("Header missing or invalid"));
    }

    assert header != null;

    jwtProvider
      .authenticate(new JsonObject().put("token", header.substring(7)))
      .compose(user -> {
        var item = Json.decodeValue(ctx.getBodyAsString(), Item.class);
        return apiService.save(item, user.get("sub"));
      })
      .onSuccess(res -> ctx.response().setStatusCode(201).end())
      .onFailure(ctx::fail);
  }

  public void getAllUserItems(RoutingContext ctx) {
    String header = ctx.request().getHeader(AUTH_HEADER);

    if (header == null || !header.startsWith(SCHEME)) {
      ctx.fail(new UnauthorizedException("Header missing or invalid"));
    }

    assert header != null;

    jwtProvider
      .authenticate(new JsonObject().put("token", header.substring(7)))
      .compose(user -> apiService.findAllUserItems(user.get("sub")))
      .onSuccess(res -> { var stream = res
        .stream()
        .map(obj -> new Item(UUID.fromString(obj.getString("_id")), UUID.fromString(obj.getString("owner")), obj.getString("name")))
        .toList();

        ctx.response().end(Json.encode(stream));
      })
      .onFailure(ctx::fail);
  }
}
