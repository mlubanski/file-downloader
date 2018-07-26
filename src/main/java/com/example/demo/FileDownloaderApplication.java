package com.example.demo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import com.example.demo.entity.DownloadStatus;
import com.example.demo.executor.DownloadExecutorBase;
import com.example.demo.repository.DownloadStatusRepository;

@SpringBootApplication
@EnableAsync(proxyTargetClass=true)
public class FileDownloaderApplication {
	
	private static final Logger LOG = LoggerFactory.getLogger(FileDownloaderApplication.class);
	
	@Autowired
	private DownloadStatusRepository repository;
	@Autowired
	private List<DownloadExecutorBase> downloadExecutors;
	
	public static void main(String[] args) {
		SpringApplication.run(FileDownloaderApplication.class, args);
	}

	@Bean
    public CommandLineRunner insertTestData() {
        return args -> { 
        	
        	for (int priority = 1; priority < 11; priority++) {				
        		DownloadStatus entity = new DownloadStatus();
        		entity.setType("HTTP");
        		entity.setUrl("http://google/images/3.avi");
        		entity.setPriority(priority);
        		
        		repository.save(entity);
			}
        	
        	for (int priority = 1; priority < 4; priority++) {				
        		DownloadStatus entity = new DownloadStatus();
        		entity.setType("S3");
        		entity.setUrl("http://google/images/3.avi");
        		entity.setPriority(priority);
        		
        		repository.save(entity);
			}
        };
    }
	
	@Bean 
	public CommandLineRunner startDownloadExecutors() {
		return args -> {
			LOG.info("Discovered {} download executors", downloadExecutors.size());
			for (DownloadExecutorBase executor: downloadExecutors) {
				LOG.info("Starting download executor: " + executor.getDownloadType());
				executor.run();
			}
		};
	}
}
