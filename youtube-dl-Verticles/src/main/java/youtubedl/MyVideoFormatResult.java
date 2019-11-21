package youtubedl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sapher.youtubedl.mapper.VideoFormat;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MyVideoFormatResult {

	public String formatId;
	public String format;
	public String ext;
	
	public MyVideoFormatResult(VideoFormat videoFormat) {
		formatId = videoFormat.formatId;
		format = videoFormat.format;
		ext = videoFormat.ext;
		/*
		format: "249 - audio only (tiny)"
		format_id: "249"
		ext: "mp4"*/
	}

}
