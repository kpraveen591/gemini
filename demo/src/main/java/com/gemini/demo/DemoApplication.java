package com.gemini.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
	    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration("127.0.0.1", 6379);
	    return new JedisConnectionFactory(redisStandaloneConfiguration);
	}
	@Bean
	public StringRedisTemplate strRedisTemplate() {
		StringRedisTemplate redisTemplate = new StringRedisTemplate();
		redisTemplate.setConnectionFactory(jedisConnectionFactory());
		return redisTemplate;
	}


}
