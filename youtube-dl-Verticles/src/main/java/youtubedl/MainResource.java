package youtubedl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;

public class MainResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainResource.class);
	private Session session;

	
	
	public Router getSubRouter(final Vertx vertx) {
		final Router subRouter = Router.router(vertx);

		// Body handler
		subRouter.route("/*").handler(BodyHandler.create());
		// Body handler
		subRouter.post("/sucks").handler(this::getTryLoginPage);
		// Body handler
		subRouter.get("/sucks").handler(this::getSuxPage);

		// Routes
		subRouter.get("/").handler(this::getHomePage);

	    SessionStore store=LocalSessionStore.create(vertx);
	    session = store.createSession(30000);
	    subRouter.route().handler(SessionHandler.create(store));
	    
		
		return subRouter;
	}

	private void getTryLoginPage(RoutingContext routingContext) {
		
		LOGGER.info("Dans TryLogin...");
		
		
		String name = routingContext.request().getFormAttribute("name");
		if (name.equals("waxpolo"))
		{
			LOGGER.info("Session Put");
			//Session session = routingContext.session();
			session.put("logged", "true");
			routingContext.redirect("/sucks");
		}
		//routingContext.response().setStatusCode(200).putHeader("content-type", "text/html").end("AIE");
		

	}

	private void getSuxPage(RoutingContext routingContext) {

		try {
			boolean isLogged = isLogged(routingContext);

			LOGGER.info("Dans getSuxPage... from a logged : " + isLogged);

			String file = readFromInputStream(getClass().getResourceAsStream("/login.html"));
			// LOGGER.info(file);
			// Envoi de la réponse
			routingContext.response().setStatusCode(200).putHeader("content-type", "text/html").end(isLogged+"<br>"+file);

		} catch (Exception e) {

			// Envoi de la réponse
			routingContext.response().setStatusCode(404).putHeader("content-type", "text/html").end(e.toString());

			e.printStackTrace();
		}

	}

	private boolean isLogged(RoutingContext routingContext) {
		//Session session = routingContext.session();
		//if (session == null)
		//	return false;
		boolean logged = Boolean.parseBoolean(session.get("logged"));
		return logged;
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