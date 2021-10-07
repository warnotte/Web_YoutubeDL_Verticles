package youtubedl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;

public class MainResource {

	private static final Logger LOGGER = LogManager.getLogger(MainResource.class);
	public static Session session;

	
	
	public Router getSubRouter(final Vertx vertx) {
		final Router subRouter = Router.router(vertx);
		
		// Body handler
		subRouter.route("/*").handler(BodyHandler.create());

		// Body handler
		//subRouter.post("/sucks").handler(this::getTryLoginPage);
		subRouter.post("/").handler(this::getTryLoginOutPage);
		
		// Routes
		subRouter.get("/").handler(this::getLoginPage);

	    SessionStore store=LocalSessionStore.create(vertx);
	    session = store.createSession(30000);
	    subRouter.route().handler(SessionHandler.create(store));
	    
	    

	    List<Route> routes = subRouter.getRoutes();
	    for (Iterator iterator = routes.iterator(); iterator.hasNext();) {
			Route route = (Route) iterator.next();
			LOGGER.info("Route : "+route);
		}
	    
		return subRouter;
	}

	private void getTryLoginOutPage(RoutingContext routingContext) {
		
		LOGGER.info("Dans TryLoginOut...");
		
		if (isLogged()==false)
		{
			String name = routingContext.request().getFormAttribute("name");
			
			if (name.equals("waxpolo"))
			{
				LOGGER.info("Login");
				session.put("logged", "true");
				session.put("name", name);
				
				routingContext.redirect("/getvideo/v1/pageguarde");
			}
			else
			{
				routingContext.redirect("/");
			}
		}
		else
		{
			LOGGER.info("Logout");
			
			session.put("logged", "false");
			session.put("name", "");
			routingContext.redirect("/");
		}
		
		
		
		//routingContext.response().setStatusCode(200).putHeader("content-type", "text/html").end("AIE");
		

	}

	private void getLoginPage(RoutingContext routingContext) {

		try {
			boolean isLogged = isLogged();

			LOGGER.info("Dans getSuxPage... from a logged : " + isLogged);

			String file = "";
			if (isLogged==false)
				file = readFromInputStream(getClass().getResourceAsStream("/login.html"));
			else
				file = readFromInputStream(getClass().getResourceAsStream("/logout.html"));
			//	Envoi de la r�ponse
			
			file = readFromInputStream(getClass().getResourceAsStream("/head.html"))+file;
			file += readFromInputStream(getClass().getResourceAsStream("/foot.html"));
			
			routingContext.response().setStatusCode(200).putHeader("content-type", "text/html").end(file);

		} catch (Exception e) {
			// Envoi de la r�ponse
			routingContext.response().setStatusCode(404).putHeader("content-type", "text/html").end(e.toString());
			e.printStackTrace();
		}

	}

	public static boolean isLogged() {
		boolean logged = Boolean.parseBoolean(session.get("logged"));
		return logged;
	}

	private void getHomePage(RoutingContext routingContext) {
		LOGGER.info("Dans homepage...");

		try {
			String file = readFromInputStream(getClass().getResourceAsStream("/main.html"));
			// LOGGER.info(file);
			// Envoi de la r�ponse
			routingContext.response().setStatusCode(200).putHeader("content-type", "text/html").end(file);
		} catch (Exception e) {

			// Envoi de la r�ponse
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