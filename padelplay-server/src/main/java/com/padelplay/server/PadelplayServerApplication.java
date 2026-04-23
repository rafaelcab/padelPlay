package com.padelplay.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PadelplayServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PadelplayServerApplication.class, args);
	}

}
