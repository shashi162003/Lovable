package com.devshashi.lovable;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootApplication
public class LovableApplication {

	public static void main(String[] args) {
		SpringApplication.run(LovableApplication.class, args);
	}

	@PostConstruct
	public void init() {
		SecurityContextHolder.setStrategyName(
				SecurityContextHolder.MODE_INHERITABLETHREADLOCAL
		);
	}
}
