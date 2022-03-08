package com.craft.externalmanagementsystemms.notfication.config;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.NamingStrategy;
import org.springframework.amqp.core.Queue;

import java.util.UUID;

public abstract class NotifConfig {

	// helper
	protected String getQueueName(String prefix) {
		return prefix + "-" + UUID.randomUUID().toString();
	}
	
	protected Queue createAnonymousQueue(String prefix) {
		return new AnonymousQueue(new NamingStrategy() {
			@Override
			public String generateName() {
				return getQueueName(prefix);
			}
		});
	}	
}
