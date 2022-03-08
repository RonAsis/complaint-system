package com.craft.externalmanagementsystemms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.backoff.UniformRandomBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RetryConfig {

	@Value("${general.retry.count:3}")
	private int defaultGeneralRetryCount;

	@Value("${general.max.backoff:100}")
	private int defaultGeneralMaxBackoff;

	@Value("${general.min.backoff:50}")
	private int defaultGeneralMinBackoff;

	
	@Bean("GeneralRetryTemplate")
	public RetryTemplate generalRetryTemplate() {
		Map<Class<? extends Throwable>, Boolean> map = new HashMap<>();
		map.put(OptimisticLockingFailureException.class, true);
		map.put(RuntimeException.class, true); 

		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(defaultGeneralRetryCount, map);
		UniformRandomBackOffPolicy backOffPolicy = new UniformRandomBackOffPolicy();
		backOffPolicy.setMaxBackOffPeriod(defaultGeneralMaxBackoff);
		backOffPolicy.setMinBackOffPeriod(defaultGeneralMinBackoff);

		RetryTemplate template = new RetryTemplate();
		template.setRetryPolicy(retryPolicy);
		template.setBackOffPolicy(backOffPolicy);
		return template;
	}
	
}
