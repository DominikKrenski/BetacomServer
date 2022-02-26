package org.dominik.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.dominik.server.errors.ApiError;
import org.dominik.server.exceptions.BaseException;
import org.dominik.server.handlers.UserHandler;
import org.dominik.server.services.definitions.ApiService;
import org.dominik.server.services.implementations.ApiServiceImpl;

import java.time.Instant;

public class MainVerticle extends AbstractVerticle {
  private static final String LOGIN_URL = "/login";
  private static final String REGISTER_URL = "/register";
  private static final String ITEMS_URLS = "/items";

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    HttpServer server = vertx.createHttpServer();

    // create ApiService instance
    ApiService apiService = new ApiServiceImpl(createMongoClient());

    // create UserHandler instance
    UserHandler userHandler = new UserHandler(apiService);

    // create router instance
    Router router = createRoutes(userHandler);

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

  private Router createRoutes(UserHandler userHandler) {
    Router router = Router.router(vertx);

    router
      .post(REGISTER_URL)
      .consumes("application/json")
      .produces("application/json")
      .handler(BodyHandler.create())
      .handler(userHandler::register)
      .failureHandler(e -> {
        Throwable failure = e.failure();

        ApiError apiError;

        if (failure instanceof BaseException) {
          var base = (BaseException) failure;
          apiError = new ApiError(base.getStatusCode(), base.getTimestamp(), base.getMessage());
        } else {
          apiError = new ApiError(500, Instant.now(), "Something went wrong. Please, try later");
        }

        e.response().setStatusCode(apiError.getStatus()).end(Json.encode(apiError));
      });

    return router;
  }

  private MongoClient createMongoClient() {

    return MongoClient.createShared(
      vertx,
      new JsonObject()
        .put("useObjectId", false)
        .put("connection_string", "mongodb+srv://betacom_owner:ajYtty9KvTBX9Jml@cluster0.0rioy.mongodb.net/betacom_db?retryWrites=true&w=majority")
    );
  }
}
