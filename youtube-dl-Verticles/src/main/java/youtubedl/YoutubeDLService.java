package youtubedl;
import java.util.Iterator;
import java.util.List;

import com.sapher.youtubedl.DownloadProgressCallback;
import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
import com.sapher.youtubedl.mapper.VideoFormat;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class YoutubeDLService
{

	static String exePath = "e:\\youtube-dl.exe";
	
	public YoutubeDLService()
	{
		super();
		init();
	}

	private void init()
	{
		YoutubeDL.setExecutablePath(exePath);
	}

	public List<VideoFormat> getFormatList(String videoID)
	{
		// Video url to download
		String videoUrl = "https://www.youtube.com/watch?v=" + videoID;

		// Destination directory
		String directory = ".";

		// Build request
		YoutubeDLRequest request = new YoutubeDLRequest(videoUrl, directory);
		request.setOption("ignore-errors"); // --ignore-errors
		request.setOption("output", "%(id)s"); // --output "%(id)s"
		request.setOption("retries", 10); // --retries 10

		String				texte	= "";
		List<VideoFormat>	formats = null;
		try
		{
			formats = YoutubeDL.getFormats(videoUrl);
			for (Iterator<VideoFormat> iterator = formats.iterator(); iterator.hasNext();)
			{
				VideoFormat format = iterator.next();

				texte += format.format + " - " + format.ext +"\r\n<br>";
				System.err.println(format.format);
				System.err.println(format.ext);
				
			}
		} catch (YoutubeDLException e)
		{
			texte = e.getMessage();
		}

		
		
		List<VideoFormat> dogs = formats;

		
		
		
		return dogs;

	}

	public boolean getVideo(String videoID, String videoFormat, String filename)
	{
		System.err.println("Video id = "+videoID);
		// Video url to download
		String videoUrl = "https://www.youtube.com/watch?v=" + videoID;

		// Destination directory
		String directory = ".";

		// Build request
		YoutubeDLRequest request = new YoutubeDLRequest(videoUrl, directory);
		request.setOption("ignore-errors"); // --ignore-errors
		request.setOption("output", filename); // --output "%(id)s"
		//request.setOption("output", "tmp/video.mp4"); // --output "%(id)s"
		request.setOption("retries", 10); // --retries 10
		
		
		
		
		System.err.println("Video format : "+videoFormat);
		
		if (videoFormat!=null)
			request.setOption("format", videoFormat); // --retries 10
		
		// Make request and return response
		try
		{
			YoutubeDLResponse response = YoutubeDL.execute(request, new DownloadProgressCallback() {
			    @Override
			    public void onProgressUpdate(float progress, long etaInSeconds) {
			        System.out.println(String.valueOf(progress) + "%");
			    }
			});
		} catch (YoutubeDLException e)
		{
			e.printStackTrace();
			return false;
		}

		System.err.println("OK");
		return true;
	}

}