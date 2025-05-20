package br.com.compass.challenge3SpringBoot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Challenge3SpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(Challenge3SpringBootApplication.class, args);
	}

}
