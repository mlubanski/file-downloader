package com.example.demo;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.DownloadStatus;
import com.example.demo.repository.DownloadStatusRepository;

@Service
public class DownloadPoller {

	@Autowired
	private DownloadStatusRepository repository;
	
	/**
	 * this method will return next file with highest priority to download
	 * 
	 * @param type - as we have more Download*Svc implementations we need to define which download type
	 * @return
	 */
	public Optional<DownloadStatus> getNextFileToDownload(DownloadType type) {
		return repository.findFirstByTypeAndStateOrderByPriorityDesc(type.toString(), DownloadState.STARTED.toString());
	}
	
}
