package com.example.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.jwt.dto.QrRequest;
import com.example.jwt.entity.QrCode;
import com.example.jwt.entity.User;
import com.example.jwt.repository.QrCodeRepository;
import com.example.jwt.repository.UserRepository;
import com.example.jwt.util.QRGenerator;

import java.time.LocalDateTime;

@Service
public class QrService {

	@Autowired
	private QrCodeRepository qrRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EncryptionService encryptionService;

	public QrCode generateQr(QrRequest request, String email) {

		User merchant = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Merchant not found"));

		String payload = merchant.getId() + "|" + request.getAmount() + "|" + System.currentTimeMillis();

		String encrypted = encryptionService.encrypt(payload);

		// Generate QR Image
		String qrImage = QRGenerator.generateQR(encrypted);

		QrCode qr = new QrCode();
		qr.setReceiverId(merchant.getId());
		qr.setAmount(request.getAmount());
		qr.setEncryptedPayload(encrypted);
		qr.setQrImage(qrImage);
		qr.setStatus("ACTIVE");
		qr.setCreatedAt(LocalDateTime.now());
		qr.setExpiryTime(LocalDateTime.now().plusMinutes(5));

		return qrRepository.save(qr);
	}
}