package youtubedl;

import io.vertx.core.Vertx;
public class App {
  public static void main(String[] args) {

    
    if (args.length!=0)
    {
    	YoutubeDL_Verticle.PORT_LISTENING = Integer.parseInt(args[0]);
    }
        
    final Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new YoutubeDL_Verticle());
  }
}