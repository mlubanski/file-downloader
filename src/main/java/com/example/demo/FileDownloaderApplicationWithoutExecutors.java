package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.demo",
excludeFilters = @Filter(type = FilterType.REGEX, pattern="com.example.demo.executor.*"))  
public class FileDownloaderApplicationWithoutExecutors {

private static final Logger LOG = LoggerFactory.getLogger(FileDownloaderApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(FileDownloaderApplicationWithoutExecutors.class, args);
	}
}
