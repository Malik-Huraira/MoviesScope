package com.moviescope;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.moviescope.security.RateLimitingFilter;

@SpringBootApplication
public class MovieScopeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieScopeApplication.class, args);
	}

}
