package com.moviescope;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MovieScopeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieScopeApplication.class, args);
	}

}
