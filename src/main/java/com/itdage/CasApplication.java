package com.itdage;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CasApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(CasApplication.class);
		application.run(args);
	}
}
