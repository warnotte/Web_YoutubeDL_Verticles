package youtubedl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.mapper.VideoFormat;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class DogResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(DogResource.class);

	private final YoutubeDLService dogService = new YoutubeDLService();

	public Router getSubRouter(final Vertx vertx) {
		final Router subRouter = Router.router(vertx);

		// Body handler
		subRouter.route("/*").handler(BodyHandler.create());

		// Routes
		subRouter.get("/").handler(event -> {
			try {
				getHomePage(event);
			} catch (Exception e) {
				event.response().setStatusCode(404).putHeader("content-type", "text").end("Invalid User");
				//event.redirect("/");
				e.printStackTrace();
			}
		});
		subRouter.get("/download/:id").handler(event -> {
			try {
				downloadAMovie(event);
			} catch (InvalidUserException e) {
				event.response().setStatusCode(404).putHeader("content-type", "text").end("Invalid User");
				//event.redirect("/");
				e.printStackTrace();
			}
		});
		// TODO : BlockingHandler ???
		subRouter.get("/get/:id/").handler(event -> {
			try {
				getAMovie(event);
			} catch (InvalidUserException e1) {
				event.response().setStatusCode(404).putHeader("content-type", "text").end("Invalid User");
				//event.redirect("/");
				e1.printStackTrace();
			}
		});
		subRouter.get("/get/:id/:format").handler(event -> {
			try {
				getAMovie(event);
			} catch (InvalidUserException e1) {
				event.response().setStatusCode(404).putHeader("content-type", "text").end("Invalid User");
				//event.redirect("/");
				e1.printStackTrace();
			}
		});
		subRouter.get("/format/:id").handler(event -> {
			try {
				getMovieFormat(event);
			} catch (InvalidUserException e) {
				event.response().setStatusCode(404).putHeader("content-type", "text").end("Invalid User");
				//event.redirect("/");
				e.printStackTrace();
			}
		});
		
		return subRouter;
	}

	private void getAMovie(final RoutingContext routingContext) throws InvalidUserException {
		
		checkUserIsvalide();
		
		LOGGER.info("Dans getAllDogs...");

		LOGGER.info(routingContext.request().toString());

		String id = routingContext.request().getParam("id");
		String format = routingContext.request().getParam("format");

		if (id == null) {
			
			routingContext.response().setStatusCode(404).putHeader("content-type", "text").end("No id passed");
			return;
		}

		System.err.println("Id = " + id);

		String filename = String.format("%s_%s.mkv", MD5(id), format);

		// TODO : Look in tmp if the file exists or not 
		
		if (new File("tmp/"+filename).exists()==false)
		{
			boolean ret;
			try {
				ret = dogService.getVideo(id, format, "tmp/" + filename);
				if (ret == false) {
					routingContext.response().setStatusCode(404).putHeader("content-type", "text").end("Problem downloading the movie");
					return;
				}
			} catch (YoutubeDLException e) {
				routingContext.response().setStatusCode(404).putHeader("content-type", "text").end(e.getMessage());
				e.printStackTrace();
				return;
			}
			
		}
		else
			System.err.println("Already downloaded !!!");
		
		System.err.println("Sending to client : " + filename);

		// Probleme sous linux avec l'extension...
		// routingContext.response().setStatusCode(200).end(filename);
		routingContext.response().setStatusCode(200).end(filename);

	}

	private void checkUserIsvalide() throws InvalidUserException {
		
		if (MainResource.isLogged())
		{
			LOGGER.debug(MainResource.session.get("name"));
			return;
		}
		throw new InvalidUserException("Nononono!");
		
	}

	private void downloadAMovie(final RoutingContext routingContext) throws InvalidUserException {

		checkUserIsvalide();
		
		String id = routingContext.request().getParam("id");

		//routingContext.response().sendFile("tmp/" + /*MD5*/(id)).end();
		routingContext.response().sendFile("tmp/" + /*MD5*/(id)).result();
	}

	private String MD5(String id) {
		String password = id;
		String md5Hex = DigestUtils.md5Hex(password).toUpperCase();
		return md5Hex;
	}

	private void getMovieFormat(final RoutingContext routingContext) throws InvalidUserException {
		
		checkUserIsvalide();
		
		LOGGER.info("Dans getMovieFormat...");

		String id = routingContext.request().getParam("id");

		if (id == null) {

			routingContext.response().setStatusCode(404).putHeader("content-type", "text").end("No id passed");
			return;
		}

		System.err.println("Id = " + id);

		// routingContext.response().setStatusCode(200).end(formatList);

		List<VideoFormat> formatList = dogService.getFormatList(id);

		List<MyVideoFormatResult> mvfrs;
		mvfrs = convert(formatList);

		// Cr�ation et remplissage de la r�ponse
		final JsonObject jsonResponse = new JsonObject();
		jsonResponse.put("formats", mvfrs);
		jsonResponse.put("my-name", "eomyname");

		// Envoi de la r�ponse
		routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
				.end(Json.encodePrettily(jsonResponse));

	}

	private List<MyVideoFormatResult> convert(List<VideoFormat> formatList) {
		List<MyVideoFormatResult> ret = new ArrayList<>();
		for (int i = 0; i < formatList.size(); i++) {
			ret.add(new MyVideoFormatResult(formatList.get(i)));

		}
		return ret;
	}

	private void getOneDog(final RoutingContext routingContext) {
		/*
		 * LOGGER.info("Dans getOneDog...");
		 * 
		 * final String id = routingContext.request().getParam("id");
		 * 
		 * final Dog dog = dogService.findById(id);
		 * 
		 * if (dog == null) { final JsonObject errorJsonResponse = new JsonObject();
		 * errorJsonResponse.put("error", "No dog can be found for the specified id:" +
		 * id); errorJsonResponse.put("id", id);
		 * 
		 * routingContext.response() .setStatusCode(404) .putHeader("content-type",
		 * "application/json") .end(Json.encode(errorJsonResponse)); return; }
		 * routingContext.response() .setStatusCode(200) .putHeader("content-type",
		 * "application/json") .end(Json.encode(dog));
		 */
	}

	private void getHomePage(RoutingContext routingContext) throws InvalidUserException {
		
		checkUserIsvalide();
		
		LOGGER.info("Dans homepage...");

		try {
			String file = readFromInputStream(getClass().getResourceAsStream("/site.html"));
			
			file = readFromInputStream(getClass().getResourceAsStream("/head.html"))+file;
			file += readFromInputStream(getClass().getResourceAsStream("/logout.html"));
			file += readFromInputStream(getClass().getResourceAsStream("/foot.html"));
			
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