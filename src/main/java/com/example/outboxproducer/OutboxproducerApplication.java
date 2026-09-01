package com.example.outboxproducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OutboxproducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OutboxproducerApplication.class, args);
	}

}
