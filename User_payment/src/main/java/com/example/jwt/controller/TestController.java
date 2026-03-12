package com.example.jwt.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestController {

	// Public API
	@GetMapping("/public")
	public String publicApi() {
		return "Public API";
	}

	// USER APIs
	@GetMapping("/user/profile")
	@PreAuthorize("hasRole('USER')")
	public String userApi() {
		return "User profile";
	}

	// MERCHANT APIs
	@GetMapping("/merchant/dashboard")
	@PreAuthorize("hasRole('MERCHANT')")
	public String merchantApi() {
		return "Merchant dashboard";
	}

	// ADMIN APIs
	@GetMapping("/admin/dashboard")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminApi() {
		return "Admin dashboard";
	}
}