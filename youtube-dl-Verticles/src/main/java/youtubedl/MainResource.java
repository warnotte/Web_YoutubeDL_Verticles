package youtubedl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import com.sapher.youtubedl.mapper.VideoFormat;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class MainResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainResource.class);

	public Router getSubRouter(final Vertx vertx) {
		final Router subRouter = Router.router(vertx);

		// Body handler
		subRouter.route("/*").handler(BodyHandler.create());

		// Routes
		subRouter.get("/").handler(this::getHomePage);

		return subRouter;
	}


	private void getHomePage(RoutingContext routingContext) {
		LOGGER.info("Dans homepage...");

		try {
			String file = readFromInputStream(getClass().getResourceAsStream("/main.html"));
			// LOGGER.info(file);
			// Envoi de la réponse
			routingContext.response().setStatusCode(200).putHeader("content-type", "text/html").end(file);
		} catch (Exception e) {

			// Envoi de la réponse
			routingContext.response().setStatusCode(404).putHeader("content-type", "text/html").end(e.toString());

			e.printStackTrace();
		}

	}

	private String readFromInputStream(InputStream inputStream) throws IOException {
		StringBuilder resultStringBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = br.readLine()) != null) {
				resultStringBuilder.append(line).append("\n");
			}
		}
		return resultStringBuilder.toString();
	}

}