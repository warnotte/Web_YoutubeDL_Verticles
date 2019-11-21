package youtubedl;
import java.io.File;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

public class MyApiVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(MyApiVerticle.class);
  @Override
  public void start() throws Exception {
	boolean f = new File("tmp").mkdir();
    LOGGER.info("Dans le start...");
    final Router router = Router.router(vertx);
    final DogResource dogResource = new DogResource();
    final Router dogSubRouter = dogResource.getSubRouter(vertx);
    router.mountSubRouter("/api/v1/youtubedl", dogSubRouter);
    router.mountSubRouter("/api/v1/pageguarde", dogSubRouter);
    vertx.createHttpServer()
        .requestHandler(router)
        .listen(8080);
  }
  @Override
  public void stop() throws Exception {
    LOGGER.info("Dans le stop...");
  }
}