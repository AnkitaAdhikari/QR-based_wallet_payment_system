package com.example.jwt.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.jwt.entity.OtpLogin;
import com.example.jwt.entity.User;
import com.example.jwt.repository.OtpLoginRepository;
import com.example.jwt.repository.UserRepository;

@Service
public class OtpService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OtpLoginRepository otpRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private JwtUtil jwtUtil;

	public String sendOtp(String email) {

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		String otp = String.valueOf(new Random().nextInt(900000) + 100000);

		OtpLogin otpLogin = new OtpLogin();
		otpLogin.setUserId(user.getId());
		otpLogin.setOtpCode(otp);
		otpLogin.setStatus("ACTIVE");
		otpLogin.setCreatedAt(LocalDateTime.now());
		otpLogin.setExpiryTime(LocalDateTime.now().plusMinutes(5));

		otpRepository.save(otpLogin);

		// ✅ Send email
		emailService.sendOtp(email, otp);

		return "OTP sent to email";
	}

	
	//verify.....
	public String verifyOtp(String email, String otp) {

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		OtpLogin otpLogin = otpRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId());

		if (!otpLogin.getOtpCode().equals(otp)) {
			throw new RuntimeException("Invalid OTP");
		}

		if (otpLogin.getExpiryTime().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("OTP expired");
		}

		otpLogin.setStatus("VERIFIED");
		otpRepository.save(otpLogin);

		// Generate JWT
		String token = jwtUtil.generateToken(user.getEmail());

		return token;
	}
}