package youtubedl;
import java.io.File;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class MyApiVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(MyApiVerticle.class);
  
	public static String IP_LISTENING = "0.0.0.0";
	public static int PORT_LISTENING = 80;

	
  @Override
  public void start() throws Exception {
	boolean f = new File("tmp").mkdir();
    LOGGER.info("Dans le start...");
    final Router router = Router.router(vertx);
    final DogResource dogResource = new DogResource();
    final Router dogSubRouter = dogResource.getSubRouter(vertx);
    final MainResource mainResource = new MainResource();
    final Router dogMainRouter = mainResource.getSubRouter(vertx);
    
    router.mountSubRouter("/", dogMainRouter);
    router.route("/*").handler(StaticHandler.create()); // -> shit sous linux ??!
    router.mountSubRouter("/getvideo/v1/youtubedl", dogSubRouter);
    router.mountSubRouter("/getvideo/v1/pageguarde", dogSubRouter);
    
    
    vertx.createHttpServer().requestHandler(router).listen(PORT_LISTENING, IP_LISTENING);
	/*
	 * vertx.createHttpServer() .requestHandler(routingContext ->
	 * routingContext.response().end("Hello World!")) .listen(8080);
	 */
	
	// TODO : Doublon avec App.java
	Runtime.getRuntime().addShutdownHook(new Thread() {
	    @Override
		public void run() {
	        vertx.close();
	    }
	});
  }
  @Override
  public void stop() throws Exception {
    LOGGER.info("Dans le stop...");
  }
}