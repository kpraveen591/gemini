package com.statestreet.ratelimit.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.statestreet.ratelimit.util.Constants;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class RateLimitService {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	private HashOperations<String, String, String> hashOperations;

	@Value("${gemini.rate.limit.requests.per.minute}")
	private String rateLimitRequestsPerMinute;

	@PostConstruct
	private void init() {
	    hashOperations = stringRedisTemplate.opsForHash();
	}

	public Long getCurrentAvailbleRequests(String key) {
	    Boolean hasKey = stringRedisTemplate.hasKey(key);
	    if (hasKey) {
	        Long value = hashOperations.increment(key, Constants.COUNT, -1l);
	        log.info("Current value for key "+key+" is "+value);
	        return value;
	    } else {
	        hashOperations.put(key, Constants.COUNT, "600");
	        stringRedisTemplate.expire(key, 1, TimeUnit.MINUTES);
	    }
	    return 600l;
	}

}
