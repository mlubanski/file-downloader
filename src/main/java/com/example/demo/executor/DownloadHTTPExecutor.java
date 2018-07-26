package com.example.demo.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.DownloadType;
import com.example.demo.entity.DownloadStatus;

@Service
public class DownloadHTTPExecutor extends DownloadExecutorBase {
	
	private static final Logger LOG = LoggerFactory.getLogger(DownloadHTTPExecutor.class);
	
	public void download(DownloadStatus downloadContext) {
		LOG.info("Downloading " + getDownloadType().toString() + " file from " + downloadContext.getUrl());
		wait(downloadTime);
	}
	
	public DownloadType getDownloadType() {
		return DownloadType.HTTP;
	}
}
