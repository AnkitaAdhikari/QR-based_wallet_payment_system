package com.example.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.jwt.dto.PaymentRequest;
import com.example.jwt.dto.ScanResponse;
import com.example.jwt.entity.QrCode;
import com.example.jwt.entity.Transaction;
import com.example.jwt.entity.User;
import com.example.jwt.repository.QrCodeRepository;
import com.example.jwt.repository.TransactionRepository;
import com.example.jwt.repository.UserRepository;

import java.time.LocalDateTime;


@Service
public class PaymentService {

	@Autowired
	private QrCodeRepository qrRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EncryptionService encryptionService;

	@Autowired
	private TransactionRepository transactionRepository;

	// SCAN QR
	public ScanResponse scanQr(Long qrId) {

		QrCode qr = qrRepository.findById(qrId).orElseThrow(() -> new RuntimeException("QR not found"));

		if (!qr.getStatus().equals("ACTIVE")) {
			throw new RuntimeException("QR already used");
		}

		if (qr.getExpiryTime().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("QR expired");
		}

		String decrypted = encryptionService.decrypt(qr.getEncryptedPayload());

		String[] parts = decrypted.split("\\|");

		Long merchantId = Long.parseLong(parts[0]);
		double amount = Double.parseDouble(parts[1]);

		User merchant = userRepository.findById(merchantId)
				.orElseThrow(() -> new RuntimeException("Merchant not found"));

		return new ScanResponse(merchant.getUsername(), amount);
	}

	// PAYMENT PROCESS
	@Transactional
	public String pay(PaymentRequest request, Authentication auth) {

		String email = auth.getName();

		User sender = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		QrCode qr = qrRepository.findById(request.getQrId()).orElseThrow(() -> new RuntimeException("QR not found"));

		if (!qr.getStatus().equals("ACTIVE")) {
			throw new RuntimeException("QR already used");
		}

		if (qr.getExpiryTime().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("QR expired");
		}

		User receiver = userRepository.findById(qr.getReceiverId())
				.orElseThrow(() -> new RuntimeException("Merchant not found"));

		double amount = qr.getAmount();

		if (sender.getBalance() < amount) {
			throw new RuntimeException("Insufficient balance");
		}

		// deduct from sender
		sender.setBalance(sender.getBalance() - amount);

		// add to merchant
		receiver.setBalance(receiver.getBalance() + amount);

		userRepository.save(sender);
		userRepository.save(receiver);

		// save transaction
		Transaction txn = new Transaction();
		txn.setTransactionRef("TXN-" + System.currentTimeMillis());
		txn.setSenderId(sender.getId());
		txn.setReceiverId(receiver.getId());
		txn.setAmount(amount);
		txn.setStatus("SUCCESS");
		txn.setTransactionTime(LocalDateTime.now());

		transactionRepository.save(txn);

		// update QR status
		qr.setStatus("USED");
		qr.setUsedAt(LocalDateTime.now());

		qrRepository.save(qr);

		return "Payment Successful";
	}
}