package youtubedl;
import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class YoutubeDL_Verticle extends AbstractVerticle {
	private static final Logger LOGGER = LogManager.getLogger(YoutubeDL_Verticle.class);
  
  
	public static String IP_LISTENING = "0.0.0.0";
	public static int PORT_LISTENING = 8080;

	
  @Override
  public void start() throws Exception {
	boolean f = new File("tmp").mkdir();
    
	LOGGER.info(String.format("Will try Starting verticle on %s:%d", IP_LISTENING, PORT_LISTENING));
	
    final Router router = Router.router(vertx);
    final DogResource dogResource = new DogResource();
    final Router dogSubRouter = dogResource.getSubRouter(vertx);
    final MainResource mainResource = new MainResource();
    final Router dogMainRouter = mainResource.getSubRouter(vertx);
    
    router.mountSubRouter("/", dogMainRouter);
    router.route("/*").handler(StaticHandler.create()); // -> shit sous linux ??!
    router.mountSubRouter("/getvideo/v1/youtubedl", dogSubRouter);
    router.mountSubRouter("/getvideo/v1/pageguarde", dogSubRouter);
    
    HttpServerOptions options = new HttpServerOptions();/*
    	    .setUseAlpn(true)
    	    .setSsl(true)
    	    .setKeyStoreOptions(new JksOptions().setPath("clientkeystore").setPassword("1969Prodigy"));
    */
    options.setLogActivity(true);
    vertx.createHttpServer(options)
    	.requestHandler(router)
    	.listen(PORT_LISTENING, IP_LISTENING)
    	.onSuccess(server -> {
    		LOGGER.info(String.format("Starting verticle on %s:%d", IP_LISTENING, server.actualPort()));
    	})
    	.onFailure(server-> {
    		LOGGER.info(String.format("Filed Starting verticle on %s:%d", IP_LISTENING, PORT_LISTENING));
    		vertx.close();
    		System.exit(-1);
    	});
    
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
    LOGGER.info("Stopping verticle");
  }
}