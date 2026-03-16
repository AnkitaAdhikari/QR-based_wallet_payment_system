package com.example.jwt.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.jwt.entity.QrLogin;
import com.example.jwt.entity.User;
import com.example.jwt.repository.QrLoginRepository;
import com.example.jwt.repository.UserRepository;

@Service
public class QrLoginService {

	@Autowired
	private QrLoginRepository qrRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtUtil jwtUtil;

	// Step 1: Generate QR
	public QrLogin generateQr() {

		String token = UUID.randomUUID().toString();

		QrLogin qr = new QrLogin();
		qr.setQrToken(token);
		qr.setStatus("PENDING");
		qr.setCreatedAt(LocalDateTime.now());
		qr.setExpiryTime(LocalDateTime.now().plusMinutes(2));

		return qrRepository.save(qr);
	}

	// Step 2: Scan QR
	public String scanQr(String token, String email) {

		QrLogin qr = qrRepository.findByQrToken(token).orElseThrow(() -> new RuntimeException("QR not found"));

		if (qr.getExpiryTime().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("QR expired");
		}

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		qr.setUserId(user.getId());
		qr.setStatus("VERIFIED");

		qrRepository.save(qr);

		return "QR Login Approved";
	}

	// Step 3: Check status
	public String checkStatus(String token) {

		QrLogin qr = qrRepository.findByQrToken(token).orElseThrow(() -> new RuntimeException("QR not found"));

//		if (qr.getExpiryTime().isBefore(LocalDateTime.now())) {
//			throw new RuntimeException("QR expired");
//		}
		if (qr.getExpiryTime().isBefore(LocalDateTime.now())) {
			qr.setStatus("EXPIRED");
			qrRepository.save(qr);
			throw new RuntimeException("QR expired");
		}

		if ("VERIFIED".equals(qr.getStatus())) {

			User user = userRepository.findById(qr.getUserId())
					.orElseThrow(() -> new RuntimeException("User not found"));

			String jwtToken = jwtUtil.generateToken(user.getEmail());

			qr.setStatus("LOGGED_IN");
			qrRepository.save(qr);

			return jwtToken;
		}

		return qr.getStatus(); // PENDING
	}
}