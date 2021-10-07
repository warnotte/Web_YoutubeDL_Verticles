package youtubedl;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sapher.youtubedl.DownloadProgressCallback;
import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
import com.sapher.youtubedl.mapper.VideoFormat;

public class YoutubeDLService
{

	private static final Logger LOGGER = LogManager.getLogger(YoutubeDLService.class);
	
	static String exePath = "e:\\youtube-dl.exe";
	
	public YoutubeDLService()
	{
		super();
		init();
	}

	private void init()
	{
		
		if (SystemUtils.IS_OS_WINDOWS)
		{
			YoutubeDL.setExecutablePath(exePath);
			LOGGER.info("OS : Windows");
		}
		else
		{
			LOGGER.info("OS : Linux");
		}
		
	}

	public List<VideoFormat> getFormatList(String videoID)
	{
		// Video url to download
		String videoUrl = "https://www.youtube.com/watch?v=" + videoID;
		videoUrl = videoID;

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
				//System.err.println(format.format);
				//System.err.println(format.ext);
				
			}
		} catch (YoutubeDLException e)
		{
			texte = e.getMessage();
		}

		List<VideoFormat> dogs = formats;

		return dogs;

	}

	public boolean getVideo(String videoID, String videoFormat, String filename) throws YoutubeDLException
	{
		LOGGER.info("Try downloading video id = "+videoID);
		// Video url to download
		String videoUrl = "https://www.youtube.com/watch?v=" + videoID;
		videoUrl = videoID;
		
		// Destination directory
		String directory = ".";

		// Build request
		YoutubeDLRequest request = new YoutubeDLRequest(videoUrl, directory);
		request.setOption("ignore-errors"); // --ignore-errors
		request.setOption("output", filename); // --output "%(id)s"
		//request.setOption("output", "tmp/video.mp4"); // --output "%(id)s"
		request.setOption("retries", 10); // --retries 10
		
		
		
		
		LOGGER.info("Video format : "+videoFormat);
		
		if (videoFormat!=null)
			request.setOption("format", videoFormat); // --retries 10
		

		YoutubeDLResponse response = YoutubeDL.execute(request, new DownloadProgressCallback() {
		    @Override
		    public void onProgressUpdate(float progress, long etaInSeconds) {
		    	LOGGER.info(String.valueOf(progress) + "%");
		    }
		});
		

		LOGGER.info("Download OK "+videoID+" status : "+response.getExitCode());
		return true;
	}

}