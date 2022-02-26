package org.dominik.server.handlers;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;
import org.dominik.server.data.User;
import org.dominik.server.exceptions.ConflictException;
import org.dominik.server.exceptions.ForbiddenException;
import org.dominik.server.services.definitions.ApiService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


public final class UserHandler {
  private final ApiService apiService;
  private final JWTAuth jwtProvider;

  public UserHandler(ApiService apiService, JWTAuth jwtProvider) {
    this.apiService = apiService;
    this.jwtProvider = jwtProvider;
  }

  public void register(RoutingContext ctx) {
    var data = Json.decodeValue(ctx.getBodyAsString(), User.class);

    apiService
      .findUserByLogin(data.getLogin())
      .compose(res -> {
        if (res == null) {
          return apiService.save(data);
        } else {
          ctx.fail(new ConflictException("User already exists"));
          return null;
        }
      })
      .onSuccess(res -> ctx.response().setStatusCode(201).end())
      .onFailure(ctx::fail);
  }

  public void login(RoutingContext ctx) {
    var data = Json.decodeValue(ctx.getBodyAsString(), User.class);

    apiService
      .findUserByLogin(data.getLogin())
      .onSuccess(res -> {
        if (res == null)
          ctx.fail(new ForbiddenException("Login or password invalid"));

        PasswordEncoder encoder = new BCryptPasswordEncoder();

        assert res != null;
        if (!encoder.matches(data.getPassword(), res.getString("password")))
          ctx.fail(new ForbiddenException("Login or password invalid"));

        String token =
          jwtProvider
            .generateToken(
              new JsonObject(),
              new JWTOptions()
                .setAlgorithm("HS512")
                .setSubject(res.getString("_id"))
                .setExpiresInMinutes(10)
            );

        ctx.response().setStatusCode(200).end(Json.encode(new JsonObject().put("token", token)));
      })
      .onFailure(ctx::fail);
  }

}
