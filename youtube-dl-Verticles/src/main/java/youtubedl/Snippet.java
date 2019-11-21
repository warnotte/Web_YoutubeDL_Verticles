package youtubedl;
import java.util.Iterator;
import java.util.List;

import com.sapher.youtubedl.DownloadProgressCallback;
import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
import com.sapher.youtubedl.mapper.VideoFormat;

/**
 * @author Warnotte Renaud
 *
 */
public class Snippet
{
	public static void main(String args[]) throws YoutubeDLException {
		
		YoutubeDL.setExecutablePath("e:\\youtube-dl.exe");
		
		// Video url to download
		String videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
		
		// Destination directory
		String directory = ".";
		
		// Build request
		YoutubeDLRequest request = new YoutubeDLRequest(videoUrl, directory);
		request.setOption("ignore-errors");		// --ignore-errors
		request.setOption("output", "%(id)s");	// --output "%(id)s"
		request.setOption("output", "video.mp4");	// --output "%(id)s"
		request.setOption("retries", 10);		// --retries 10
		
		
		List<VideoFormat> formats = YoutubeDL.getFormats(videoUrl);
		for (Iterator<VideoFormat> iterator = formats.iterator(); iterator.hasNext();)
		{
			VideoFormat format = iterator.next();
			System.err.println(format.format);
			System.err.println(format.ext);
		}
		
		
		// Make request and return response
		YoutubeDLResponse response = YoutubeDL.execute(request, new DownloadProgressCallback() {
	        @Override
	        public void onProgressUpdate(float progress, long etaInSeconds) {
	            System.out.println(String.valueOf(progress) + "%");
	        }
	    });
		
		
		// Response
		String stdOut = response.getOut(); // Executable output
		
		System.err.println(stdOut);
		
		System.err.println("Dir : "+response.getDirectory());
	
	}
}/**
 * 
 */

