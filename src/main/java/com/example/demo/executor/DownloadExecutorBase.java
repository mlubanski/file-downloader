package com.example.demo.executor;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;

import com.example.demo.DownloadPoller;
import com.example.demo.DownloadState;
import com.example.demo.DownloadType;
import com.example.demo.entity.DownloadStatus;
import com.example.demo.queue.RabbitMessageSenderAdapter;
import com.example.demo.repository.DownloadStatusRepository;

public abstract class DownloadExecutorBase implements Runnable {
	
	private static final Logger LOG = LoggerFactory.getLogger(DownloadExecutorBase.class);
	
	@Value("${fd.download.time}")
    Integer downloadTime;
	@Value("${fd.waitForNextAvailableFile.time}")
    private Integer waitForNextAvailableFileTime;
	
	@Autowired
	private DownloadStatusRepository repository;
	@Autowired
	private RabbitMessageSenderAdapter rabbitMessageSender;
	@Autowired
	private DownloadPoller downloadPoller;
	
	public abstract void download(DownloadStatus downloadContext);
	public abstract DownloadType getDownloadType();
	
	@Async
	public void run() {
		while (true) {
			download();
		}
	}
	
	private void download() {
		DownloadStatus downloadContext = getFileToDownlaod();
		try {
			tryDownload(downloadContext);
		} catch (Throwable e) {
			if(downloadContext != null) {
				changeDownloadStatusToError(downloadContext);
			}
		}
	}
	
	private DownloadStatus getFileToDownlaod() {
		Optional<DownloadStatus> nextFileToDownload = downloadPoller.getNextFileToDownload(getDownloadType());
		
		//if there is no file to download than wait fes sec and check again
		while(! nextFileToDownload.isPresent()) {
			wait(waitForNextAvailableFileTime);
			nextFileToDownload = downloadPoller.getNextFileToDownload(getDownloadType());
		}
		
		return nextFileToDownload.get();
	}

	private void tryDownload(DownloadStatus downloadContext) {
		changeDownloadStatusToInProgress(downloadContext);
		download(downloadContext);
		changeDownloadStatusToFinished(downloadContext);
	}

	private void changeDownloadStatusToFinished(DownloadStatus downloadContext) {
		downloadContext.setState(DownloadState.FINISHED.toString());
		repository.save(downloadContext);
		rabbitMessageSender.downloadFinished(downloadContext);
	}

	private void changeDownloadStatusToInProgress(DownloadStatus downloadContext) {
		downloadContext.setState(DownloadState.PROGRESS.toString());
		repository.save(downloadContext);
		rabbitMessageSender.downloadProgress(downloadContext);
	}

	private void changeDownloadStatusToError(DownloadStatus downloadContext) {
		downloadContext.setState(DownloadState.ERROR.toString());
		repository.save(downloadContext);
		rabbitMessageSender.downloadError(downloadContext);
	}
	
	void wait(int n) {
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
