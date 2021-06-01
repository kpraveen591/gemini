package com.statestreet.ratelimit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.statestreet.ratelimit.service.RateLimitService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class RateLimitController {

	@Autowired
	private RateLimitService rateLimitService;

	@Value("${gemini.api.key}")
	private String gemini_api_key;

	@GetMapping(value = "/ratelimit")
    public ResponseEntity<Long> getCurrentAvailbleRequests() {
		log.info("Got request for checking the rate limit");
		Long currentAvailbleRequests = rateLimitService.getCurrentAvailbleRequests(gemini_api_key);
		return ResponseEntity.ok(currentAvailbleRequests);
    }

}
