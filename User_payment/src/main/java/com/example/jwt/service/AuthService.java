package com.example.jwt.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.jwt.dto.LoginRequest;
import com.example.jwt.dto.RegisterRequest;
import com.example.jwt.dto.VerificationRequest;
import com.example.jwt.entity.User;
import com.example.jwt.repository.UserRepository;

@Service
public class AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authManager;

	// SIGNUP
	public String register(RegisterRequest request) {

		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new RuntimeException("Email already registered");
		}

		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole("ROLE_" + request.getRole().toUpperCase());
		user.setEnabled(false);

		String code = String.valueOf((int) (Math.random() * 900000) + 100000);

		user.setVerificationCode(code);
		user.setVerificationExpiry(LocalDateTime.now().plusMinutes(10));

		userRepository.save(user);

		// Send email
		emailService.sendVerificationCode(user.getEmail(), user.getUsername(), code);

		return jwtUtil.generateToken(user.getEmail());
	}

	// LOGIN
	public String login(LoginRequest request) {

		authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (!user.getEnabled()) {
			throw new RuntimeException("Account not verified");
		}

		return jwtUtil.generateToken(user.getEmail());
	}

	// VERIFY ACCOUNT
	public void verifyAccount(VerificationRequest request) {

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (user.getVerificationCode() == null || !user.getVerificationCode().equals(request.getCode())) {

			throw new RuntimeException("Invalid verification code");
		}

		if (user.getVerificationExpiry().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Verification code expired");
		}

		user.setEnabled(true);
		user.setVerificationCode(null);
		user.setVerificationExpiry(null);

		userRepository.save(user);
	}
}