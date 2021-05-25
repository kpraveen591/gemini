package com.gemini.demo;

import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

@Service
@Slf4j
public class GeminiService {
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	private HashOperations<String, String, String> hashOperations;

	@PostConstruct
	private void init() {
	    hashOperations = stringRedisTemplate.opsForHash();
	}

	@Value("${gemini.api.key}")
	private String api_key;

	@Value("${gemini.api.secret}")
	private String api_secret;

	@Value("${gemini.base.url}")
	private String geminiBaseUrl;

	@Value("${gemini.newbroker.url}")
	private String newBrokerUrl;

	@Value("${rate.limit.error.message}")
	private String rateLimitErrorMessage;

	@Value("${gemini.rate.limit.requests.per.minute}")
	private String rateLimitRequestsPerMinute;

	public String createNewBroker() {

		if (!isRequestAllowed(api_key)) {
			return rateLimitErrorMessage;
		}
		String response = "";
		try {
	    	HttpHeaders headers = getHttpHeaders();
	    	HttpEntity<GeminiNewBrokerorderRequest> requestEntity = new HttpEntity<>(headers);
	    	//response = "success";
	    	response = restTemplate.exchange(geminiBaseUrl + newBrokerUrl, HttpMethod.POST, requestEntity, String.class).getBody();
	    	log.info("### response "+response);
		} catch (Exception exception) {
			log.error("Exception while fetching data from Gemini. Message: "+exception.getMessage());
			return "Exception while fetching data from Gemini. Message: "+exception.getMessage();
		}
		return response;
	}

	public String createNewBrokerWithWebClient() throws Exception {

		if (!isRequestAllowed(api_key)) {
			return rateLimitErrorMessage;
		}

		int nonce = Integer.parseInt(String.valueOf(new Date().getTime()).substring(4));
		Random random = new Random(); 
    	GeminiNewBrokerorderRequest gmbr = new GeminiNewBrokerorderRequest(newBrokerUrl, nonce, "R485E04Q","Z4929ZDY",
    			"ethusd", String.valueOf(random.nextDouble()), 1.0, String.valueOf(Math.abs(random.nextInt())),"sell");
    	String requestBodyString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(gmbr);

    	log.info("###### requestBodyString "+requestBodyString);
    	String encodedPayloadString = Base64.getEncoder().encodeToString(requestBodyString.getBytes());
    	String signature = new HmacUtils(HmacAlgorithms.HMAC_SHA_384, api_secret).hmacHex(encodedPayloadString);
    	//log.info("##### Signature "+signature);

    	String responseString = "";
		WebClient.RequestHeadersSpec<?> headersSpec = WebClient.builder().baseUrl(geminiBaseUrl)
				.defaultHeader(HttpHeaders.CACHE_CONTROL, "no-cache")
				.defaultHeader(HttpHeaders.CONTENT_LENGTH, "0")
				.build().post().uri(newBrokerUrl)
				.bodyValue(BodyInserters.fromValue(""))
				.header("X-GEMINI-APIKEY", api_key)
				.header("X-GEMINI-PAYLOAD", encodedPayloadString)
				.header("X-GEMINI-SIGNATURE", signature);
		
		Mono<String> responseMono = headersSpec.exchangeToMono(response -> handleClientResponse(response));
		responseString = responseMono.block();
		return responseString;
	}

	public HttpClient getHttpClient() {
		return HttpClient.create().secure()
				.proxy(proxyOptions -> proxyOptions.type(ProxyProvider.Proxy.HTTP)
						                           .host("proxy.statestr.com")
						                           .port(80)
						                           .build());
	}

	public Mono<String> handleClientResponse(ClientResponse clientResponse) {
		if (clientResponse.statusCode().is2xxSuccessful()) {
			return clientResponse.bodyToMono(String.class);
		} else if (clientResponse.statusCode().is4xxClientError()) {
			return Mono.just(String.format("Error response %s, %s",
					clientResponse.statusCode(), clientResponse.bodyToMono(String.class)));
		} else {
			return clientResponse.createException().flatMap(Mono::error);
		}
	}

	public boolean isRequestAllowed(String key) {
	    Boolean hasKey = stringRedisTemplate.hasKey(key);
	    if (hasKey) {
	        Long value = hashOperations.increment(key, Constants.COUNT, -1l);
	        log.info("Current value for key "+key+" is "+value);
	        return value > 0;
	    } else {
	        hashOperations.put(key, Constants.COUNT, "600");
	        stringRedisTemplate.expire(key, 1, TimeUnit.MINUTES);
	    }
	    return true;
	}

	private HttpHeaders getHttpHeaders() throws Exception {
		int nonce = Integer.parseInt(String.valueOf(new Date().getTime()).substring(3));
		Random random = new Random(); 
    	GeminiNewBrokerorderRequest gmbr = new GeminiNewBrokerorderRequest(newBrokerUrl, nonce, "R485E04Q","Z4929ZDY",
    			"ethusd", String.valueOf(random.nextDouble()), 1.0, String.valueOf(Math.abs(random.nextInt())),"sell");
    	String requestBodyString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(gmbr);
    	log.info("###### requestBodyString "+requestBodyString);

    	String encodedPayloadString = Base64.getEncoder().encodeToString(requestBodyString.getBytes());
    	//log.info("###### encodedPayloadString "+encodedPayloadString);
    	String signature = new HmacUtils(HmacAlgorithms.HMAC_SHA_384, api_secret).hmacHex(encodedPayloadString);
    	//log.info("##### Signature "+signature);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "text/plain");
		headers.add("Content-Length", "0");
		headers.add("X-GEMINI-APIKEY", api_key);
		headers.add("X-GEMINI-PAYLOAD", encodedPayloadString);
		headers.add("X-GEMINI-SIGNATURE", signature);
		headers.add("Cache-Control", "no-cache");
		return headers;
	}
}
