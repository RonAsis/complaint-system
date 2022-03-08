package com.craft.externalmanagementsystemms.notfication.config;

import com.craft.complaint.management.api.notfication.ComplaintManagementNotificationMessageQIF;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
public class ComplaintManagementConfig{

    private static final String QUEUE_NAME_PREFIX = "EMS-COMPLAINT-MANAGEMENT";

    @Bean
    public TopicExchange complaintManagementExchange() {
        log.info("Creating exchange: {}", ComplaintManagementNotificationMessageQIF.EXCHANGE_NAME);
        return new TopicExchange(ComplaintManagementNotificationMessageQIF.EXCHANGE_NAME);
    }

    @Bean
    public Queue complaintManagementQueue() {
        return new Queue(QUEUE_NAME_PREFIX);
    }

    @Bean
    public Binding bindingToscaEngineMessages(TopicExchange complaintManagementExchange, Queue complaintManagementQueue) {
        return BindingBuilder.bind(complaintManagementQueue).to(complaintManagementExchange).with(ComplaintManagementNotificationMessageQIF.ROUTING_KEY_COMPLAINTS);
    }
}
