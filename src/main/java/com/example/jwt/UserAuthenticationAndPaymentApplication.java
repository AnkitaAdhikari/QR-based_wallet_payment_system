package com.example.jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserAuthenticationAndPaymentApplication {
	public static void main(String[] args) {
		SpringApplication.run(UserAuthenticationAndPaymentApplication.class, args);
		System.out.println("Hello!.... This is a Payment Application");
	}
}
