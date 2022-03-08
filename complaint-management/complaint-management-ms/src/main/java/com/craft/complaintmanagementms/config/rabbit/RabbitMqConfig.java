package com.craft.complaintmanagementms.config.rabbit;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

	@Bean
	public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
		return rabbitTemplate;
	}
	@Bean
	public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
		Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
		jackson2JsonMessageConverter.setAssumeSupportedContentType(false);
		return jackson2JsonMessageConverter;
	}
	@Autowired
	public void rabbitListenerContainerFactory(SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory) {
		rabbitListenerContainerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
	}

}
