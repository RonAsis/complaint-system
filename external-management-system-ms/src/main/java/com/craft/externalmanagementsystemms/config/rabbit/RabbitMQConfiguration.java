package com.craft.externalmanagementsystemms.config.rabbit;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A configuration class for rabbit
 */
@Configuration
public class RabbitMQConfiguration {
	
	/**
	 * RabbitMQ host
	 */
	@Value("${rabbitmq.host}")
	private String host;
	
	/**
	 * RabbitMQ port
	 */
	@Value("${rabbitmq.port}")
	private String port;
	
	/**
	 * RabbitMQ user
	 */
	@Value("${rabbitmq.user}")
	private String user;
	
	/**
	 * RabbitMQ password
	 */
	@Value("${rabbitmq.password}")
	private String password;

	/**
	 * for local run without credentials
	 */
	@Value("${rabbitmq.credentials.enable:true}")
	private Boolean credentialsEnable;
	
	/**
	 * @return a connection factory to connect to rabbit server
	 */
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
		connectionFactory.setPort(Integer.parseInt(port));
		if (credentialsEnable) {
			connectionFactory.setUsername(user);
			connectionFactory.setPassword(password);
		}
		return connectionFactory;
	}
}
