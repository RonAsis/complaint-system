package com.craft.complaintmanagementms.notfication.config;

import com.craft.complaint.management.api.notfication.ComplaintManagementNotificationMessageQIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * create Complaint Management EXCHANGE
 */
@Configuration
@Slf4j
public class NotificationComplaintManagementConfig {

	@Bean
	public TopicExchange toscaTopicExchange() {
		log.info("Creating Complaint Management Topic exchange: {}", ComplaintManagementNotificationMessageQIF.EXCHANGE_NAME);
		return new TopicExchange(ComplaintManagementNotificationMessageQIF.EXCHANGE_NAME);
	}
}
