package com.example.iam2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@SpringBootApplication
public class Iam2Application {

	public static void main(String[] args) {
		SpringApplication.run(Iam2Application.class, args);
	}

}
