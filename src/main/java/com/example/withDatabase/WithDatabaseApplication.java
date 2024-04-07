package com.example.withDatabase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class WithDatabaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(WithDatabaseApplication.class, args);
	}

}
