package com.example.demo.resource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.DownloadState;
import com.example.demo.entity.DownloadStatus;
import com.example.demo.exception.DownloadNotFoundException;
import com.example.demo.queue.RabbitMessageSenderAdapter;
import com.example.demo.repository.DownloadStatusRepository;

/**
 * for demo purposes to speed up work I decided to do not create controller/svc where some logic will be handled
 * I am accessing repository directly here
 * 
 * @author mlubanski
 *
 */
@RestController
@RequestMapping(path="/downloads", produces = {"application/json"})
public class DownloadStatusResource {
	
	@Autowired
	private DownloadStatusRepository repository;
	@Autowired
	private RabbitMessageSenderAdapter rabbitMessageSender;
	
	@RequestMapping(method=RequestMethod.GET)
	public List<DownloadStatus> listAllDownloads() {
		return repository.findAll();
	}
	
	@RequestMapping(path="/{id}", method=RequestMethod.GET)
	public DownloadStatus getDownload(@PathVariable("id") String id) {
		return tryGetDownload(id);
	}
	
	private DownloadStatus tryGetDownload(String id) {
		try {
			Optional<DownloadStatus> download = repository.findById(UUID.fromString(id));
			return download.orElseThrow(() -> new DownloadNotFoundException());
		} catch (IllegalArgumentException e) {
			throw new DownloadNotFoundException();
		}
	}
	
	@RequestMapping(path="/{id}", method=RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@Transactional
	public void abortDownload(@PathVariable("id") String id) {
		DownloadStatus download = tryGetDownload(id);
		
		//for simplicity I decided we can abort only STARTED downloads which are not yet in progress
		if(download.getState().equals(DownloadState.STARTED.toString())) {
			//I decided do not delete entry from database in order to have some audit
			//we could in future add new state: ABORTED
			download.setState(DownloadState.ERROR.toString());
			repository.save(download);
		}
	}
	
	@RequestMapping(method=RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	@Transactional
	public DownloadStatus createNewDownload(@RequestBody DownloadStatus download) {
		download = repository.save(download);
		rabbitMessageSender.downloadStarted(download);
		return download;
	}
	
	
}
