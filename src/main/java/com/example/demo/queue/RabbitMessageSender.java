package com.example.demo.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RabbitMessageSender implements IRabbitMessageSender {
	
	private static final Logger LOG = LoggerFactory.getLogger(RabbitMessageSender.class);
	
	void publish(String message, String exchange) {
		LOG.info("{} --> {}", exchange, message);
	}
}
