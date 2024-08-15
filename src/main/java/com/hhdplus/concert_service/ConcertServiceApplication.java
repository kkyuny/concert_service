package com.hhdplus.concert_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class ConcertServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConcertServiceApplication.class, args);
	}

}
