package org.dominik.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.dominik.server.errors.ApiError;
import org.dominik.server.exceptions.BaseException;
import org.dominik.server.handlers.ItemHandler;
import org.dominik.server.handlers.UserHandler;
import org.dominik.server.services.definitions.ApiService;
import org.dominik.server.services.implementations.ApiServiceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Properties;

public class MainVerticle extends AbstractVerticle {
  private static final String LOGIN_URL = "/login";
  private static final String REGISTER_URL = "/register";
  private static final String ITEMS_URL = "/items";

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    HttpServer server = vertx.createHttpServer();

    // create ApiService instance
    ApiService apiService = new ApiServiceImpl(createMongoClient());

    // create UserHandler instance
    UserHandler userHandler = new UserHandler(apiService, createJwtProvider());

    // create ItemHandler instance
    ItemHandler itemHandler = new ItemHandler(apiService, createJwtProvider());

    // create router instance
    Router router = createRoutes(userHandler, itemHandler);

    server
      .requestHandler(router)
      .listen(8080, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          System.out.println("HTTP server is listening on port 8080");
        } else {
          startPromise.fail(http.cause());
        }
      });
  }

  private Router createRoutes(UserHandler userHandler, ItemHandler itemHandler) {
    Router router = Router.router(vertx);

    router
      .post(REGISTER_URL)
      .consumes("application/json")
      .produces("application/json")
      .handler(BodyHandler.create())
      .handler(userHandler::register)
      .failureHandler(this::handleFailure);

    router
      .post(LOGIN_URL)
      .consumes("application/json")
      .produces("application/json")
      .handler(BodyHandler.create())
      .handler(userHandler::login)
      .failureHandler(this::handleFailure);

    router
      .post(ITEMS_URL)
      .consumes("application/json")
      .produces("application/json")
      .handler(BodyHandler.create())
      .handler(itemHandler::save)
      .failureHandler(this::handleFailure);

    router
      .get(ITEMS_URL)
      .produces("application/json")
      .handler(itemHandler::getAllUserItems)
      .failureHandler(this::handleFailure);

    return router;
  }

  private void handleFailure(RoutingContext ctx) {
    Throwable failure = ctx.failure();
    ApiError apiError;

    if (failure instanceof BaseException) {
      var base = (BaseException) failure;
      apiError = new ApiError(base.getStatusCode(), base.getTimestamp(), base.getMessage());
    } else {
      apiError = new ApiError(500, Instant.now(), "Something went wrong. Please, try later.");
    }

    ctx.response().setStatusCode(apiError.getStatus()).end(Json.encode(apiError));
  }

  private MongoClient createMongoClient() {
    Properties props = readProps();

    assert props != null;
    return MongoClient.createShared(
      vertx,
      new JsonObject()
        .put("useObjectId", false)
        .put("connection_string", props.getProperty("connection_string"))
    );
  }

  private JWTAuth createJwtProvider() {
    Properties props = readProps();
    assert props != null;
    JWTAuthOptions config = new JWTAuthOptions()
      .setKeyStore(
        new KeyStoreOptions()
          .setPath("store.pkcs12")
          .setPassword(props.getProperty("keystore_password"))
      );

    return JWTAuth.create(vertx, config);
  }

  private Properties readProps() {
    try (InputStream input = MainVerticle.class.getClassLoader().getResourceAsStream("app.properties")) {
      Properties props = new Properties();

      if (input == null) {
        return null;
      }

      props.load(input);
      return props;

    } catch (IOException ex) {
      ex.printStackTrace();
      return null;
    }
  }
}
