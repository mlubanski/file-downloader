package com.example.demo.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.DownloadState;
import com.example.demo.entity.DownloadStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RabbitMessageSenderAdapter  {
	
	private static final Logger LOG = LoggerFactory.getLogger(RabbitMessageSenderAdapter.class);

	@Autowired
	private RabbitMessageSender rabbitMessageSender;
	
	public void downloadStarted(DownloadStatus downloadContext) {
		String json = convertToJson(downloadContext);
		rabbitMessageSender.publish(json, DownloadState.STARTED.toString());
	}
	
	public void downloadProgress(DownloadStatus downloadContext) {
		String json = convertToJson(downloadContext);
		rabbitMessageSender.publish(json, DownloadState.PROGRESS.toString());
	}
	
	public void downloadFinished(DownloadStatus downloadContext) {
		String json = convertToJson(downloadContext);
		rabbitMessageSender.publish(json, DownloadState.FINISHED.toString());
	}
	
	public void downloadError(DownloadStatus downloadContext) {
		String json = convertToJson(downloadContext);
		rabbitMessageSender.publish(json, DownloadState.ERROR.toString());
	}
	
	private String convertToJson(DownloadStatus downloadContext) {
		try {
			return tryConvertToJson(downloadContext);
		} catch (JsonProcessingException e) {
			LOG.error("Can't deserialize DownloadContext to JSON: " + downloadContext);
			return downloadContext.toString();
		}
	}
	
	private String tryConvertToJson(DownloadStatus downloadContext) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(downloadContext);
	}
}
