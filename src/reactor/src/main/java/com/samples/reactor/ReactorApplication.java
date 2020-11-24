package com.samples.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@RestController
@SpringBootApplication
public class ReactorApplication {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@RequestMapping("/1")
	public Mono<String> reactor1() {
		LOGGER.info("Thread # {}: enter reactor1", Thread.currentThread().getId());
		return Mono.just(42)
			.map(x -> {
				LOGGER.info("Thread # {}: map", Thread.currentThread().getId());
				return "value: " + x;
			})
			.log()
			.doOnNext (x -> {
				LOGGER.info("Thread # {}: doOnNext {}", Thread.currentThread().getId(), x);
			});
	}

	@RequestMapping("/2")
	public Mono<String> netty1() {
		String url = System.getenv("REST_URL");
		if (url == null || url.isEmpty()) {
			url = "https://www.bing.com";
		}
		LOGGER.info("Thread # {}: enter netty1", Thread.currentThread().getId());
		return HttpClient.create()
					 	 .doOnConnect(x -> {
							LOGGER.info("Thread # {}: doOnConnect", Thread.currentThread().getId());
						 })
						 .doOnConnected(x -> {
							 LOGGER.info("Thread # {}: doOnConnected", Thread.currentThread().getId());
						 })
				  		 .get()
				  		 .uri(url)
				  		 .responseContent()
				  		 .aggregate()
				  		 .asString()
						 .log("http-client")
						 .doOnNext (x -> {
							LOGGER.info("Thread # {}: doOnNext {}", Thread.currentThread().getId(), x.length());
						 });
	}

	public static void main(String[] args) {
		SpringApplication.run(ReactorApplication.class, args);
	}
}
