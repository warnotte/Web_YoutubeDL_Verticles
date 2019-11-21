package youtubedl;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
    subRouter.get("/").handler(this::getHomePage);
    subRouter.get("/download/:id").handler(this::downloadAMovie);
    subRouter.get("/get/:id/").handler(this::getAMovie);
    subRouter.get("/get/:id/:format").handler(this::getAMovie);
    subRouter.get("/format/:id").handler(this::getMovieFormat);
    
    return subRouter;
  }

  private void getAMovie(final RoutingContext routingContext) {
	  LOGGER.info("Dans getAllDogs...");
	    
	  LOGGER.info(routingContext.request().toString());

	    String id = routingContext.request().getParam("id");
	    String format = routingContext.request().getParam("format");
	    
	    if (id == null) {
	   	 
	        routingContext.response()
	            .setStatusCode(404)
	            .putHeader("content-type", "text")
	            .end("No id passed");
	        return;
	      }

	    System.err.println("Id = "+id);
	    
	    String filename = String.format("%s_%s.mkv",id, format);
	    
	    boolean ret = dogService.getVideo(id, format, "tmp/"+filename);
	    if (ret ==false) {
	   	 
	        routingContext.response()
	            .setStatusCode(404)
	            .putHeader("content-type", "text")
	            .end("Problem downloading the movie");
	        return;
	    }
	    
	    System.err.println("Sending to client : "+filename);
	    
	    // Probleme sous linux avec l'extension...
	    // routingContext.response().setStatusCode(200).end(filename);
	    routingContext.response().setStatusCode(200).end(filename);
	    
	  }
  
  
  private void downloadAMovie(final RoutingContext routingContext) {
	  
	  String id = routingContext.request().getParam("id");
	  
	  routingContext.response().sendFile("tmp/"+id).end();
  }
  
  
  private void getMovieFormat(final RoutingContext routingContext) {
	    LOGGER.info("Dans getMovieFormat...");

	    String id = routingContext.request().getParam("id");
	    
	    if (id == null) {
	 
	        routingContext.response()
	            .setStatusCode(404)
	            .putHeader("content-type", "text")
	            .end("No id passed");
	        return;
	    }

	    
	    System.err.println("Id = "+id);
	    

	//    routingContext.response().setStatusCode(200).end(formatList);
		
	    List<VideoFormat> formatList = dogService.getFormatList(id);
	  
	    List<MyVideoFormatResult> mvfrs;
	    mvfrs = convert(formatList);
	    
	    
	 //		Création et remplissage de la réponse
	 		final JsonObject jsonResponse = new JsonObject();
	 		jsonResponse.put("formats", mvfrs);
	 		jsonResponse.put("my-name", "Wax78");

	 		// Envoi de la réponse
	 		routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(Json.encodePrettily(jsonResponse));
	 		
	    
	   
	  }

  private List<MyVideoFormatResult> convert(List<VideoFormat> formatList) {
	  List<MyVideoFormatResult> ret = new ArrayList<>();
	for (int i = 0; i < formatList.size(); i++) {
		ret.add(new MyVideoFormatResult(formatList.get(i)));
		
	}
	return ret;
}

private void getOneDog(final RoutingContext routingContext) {
    /*LOGGER.info("Dans getOneDog...");

    final String id = routingContext.request().getParam("id");

    final Dog dog = dogService.findById(id);

    if (dog == null) {
      final JsonObject errorJsonResponse = new JsonObject();
      errorJsonResponse.put("error", "No dog can be found for the specified id:" + id);
      errorJsonResponse.put("id", id);

      routingContext.response()
          .setStatusCode(404)
          .putHeader("content-type", "application/json")
          .end(Json.encode(errorJsonResponse));
      return;
    }
    routingContext.response()
        .setStatusCode(200)
        .putHeader("content-type", "application/json")
        .end(Json.encode(dog));
        */
  }
  
  
  private void getHomePage(RoutingContext routingContext) {
	  LOGGER.info("Dans homepage...");
	  
	try
	{
		String file = readFromInputStream(getClass().getResourceAsStream("/site.html"));
		//LOGGER.info(file);
		  // Envoi de la rÃ©ponse
		  routingContext.response()
		      .setStatusCode(200)
		      .putHeader("content-type", "text/html")
		      .end(file);
	} 
	catch (Exception e)
	{
		
		  // Envoi de la rÃ©ponse
		  routingContext.response()
		      .setStatusCode(404)
		      .putHeader("content-type", "text/html")
		      .end(e.toString());
		  
		e.printStackTrace();
	}

	}


private String readFromInputStream(InputStream inputStream)
		  throws IOException {
		    StringBuilder resultStringBuilder = new StringBuilder();
		    try (BufferedReader br
		      = new BufferedReader(new InputStreamReader(inputStream))) {
		        String line;
		        while ((line = br.readLine()) != null) {
		            resultStringBuilder.append(line).append("\n");
		        }
		    }
		  return resultStringBuilder.toString();
		}
  
}