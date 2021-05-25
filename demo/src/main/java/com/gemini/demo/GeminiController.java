package com.gemini.demo;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class GeminiController {

	@Autowired
	private GeminiService geminiService;
	
	private final Bucket bucket;
	
	public GeminiController() {
        Bandwidth limit = Bandwidth.classic(600, Refill.greedy(600, Duration.ofMinutes(1)));
        this.bucket = Bucket4j.builder()
            .addLimit(limit)
            .build();
	}

	@GetMapping(value = "/v1/clearing/broker/new")
    public ResponseEntity<String> createBroker() throws Exception {
		if (bucket.tryConsume(1)) {
			String response = geminiService.createNewBroker();
			String responseUsingWebClient = geminiService.createNewBrokerWithWebClient();
			log.info("### responseUsingWebClient "+responseUsingWebClient);
	        return ResponseEntity.ok(response);
		}
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

	@GetMapping(value = "/v1/clearing/broker/new/bulk")
    public ResponseEntity<String> createBrokerBulk() {
		for (int i=0; i<700; i++) {
			  geminiService.createNewBroker();
		}
        return ResponseEntity.ok("Done");
    }
}
