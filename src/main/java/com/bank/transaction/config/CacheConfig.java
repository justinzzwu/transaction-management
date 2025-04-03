package com.bank.transaction.config;

import java.util.Map;

import com.bank.transaction.util.JsonUtils;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

	@Bean("transactionListKeyGenerator")
	public KeyGenerator transactionListKeyGenerator() {
		return (target, method, params) -> {
			StringBuilder key = new StringBuilder();
			for (Object param : params) {
				if (param instanceof Map) {
					// sort the map by key to ensure consistent key generation
					key.append(JsonUtils.toJson(param)).append(":");
				} else {
					key.append(param).append(":");
				}
			}
			return key.toString();
		};
	}
}