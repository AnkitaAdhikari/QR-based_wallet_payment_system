package com.example.jwt.controller;

import com.example.jwt.dto.*;
import com.example.jwt.entity.QrLogin;
import com.example.jwt.entity.QrLoginResponse;
import com.example.jwt.service.AuthService;
import com.example.jwt.service.OtpService;
import com.example.jwt.service.QrLoginService;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	@Autowired
	private QrLoginService qrService;

	@PostMapping("/signup")
	public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

		String token = authService.register(request);

		return ResponseEntity.ok(Collections.singletonMap("token", token));
	}

	@PostMapping("/verify")
	public ResponseEntity<?> verifyAccount(@RequestBody VerificationRequest request) {

		authService.verifyAccount(request);

		return ResponseEntity.ok("Account verified successfully!");
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {

		String token = authService.login(request);

		return ResponseEntity.ok(Collections.singletonMap("token", token));
	}

	@Autowired
	private OtpService otpService;

	@PostMapping("/send-otp")
	public String sendOtp(@RequestBody SendOtpRequest request) {

		return otpService.sendOtp(request.getEmail());
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {

		String token = otpService.verifyOtp(request.getEmail(), request.getOtp());

		return ResponseEntity.ok(Collections.singletonMap("token", token));
	}

	// LOGIN BY QR

	// Generate QR
	@GetMapping("/qr-login")
	public ResponseEntity<QrLoginResponse> generateQr() {

		QrLogin qr = qrService.generateQr();

		QrLoginResponse res = new QrLoginResponse();
		res.setToken(qr.getQrToken());

		return ResponseEntity.ok(res);
	}

	// Scan QR
	@PostMapping("/scan_login_qr")
	public ResponseEntity<?> scanQr(@RequestBody QrScanRequest request) {
		return ResponseEntity.ok(qrService.scanQr(request.getToken(), request.getEmail()));
	}

	// Check login status
	@GetMapping("/qr-status/{token}")
	public ResponseEntity<?> checkStatus(@PathVariable String token) {
		return ResponseEntity.ok(qrService.checkStatus(token));
	}
}