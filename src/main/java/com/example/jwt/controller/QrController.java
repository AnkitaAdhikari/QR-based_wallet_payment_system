package com.example.jwt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.jwt.dto.QrRequest;
import com.example.jwt.entity.QrCode;
import com.example.jwt.service.QrService;

@RestController
@RequestMapping("/merchant")
public class QrController {

	@Autowired
	private QrService qrService;

	@PostMapping("/generate-qr")
	@PreAuthorize("hasRole('MERCHANT')")
	public ResponseEntity<?> generateQr(@RequestBody QrRequest request, Authentication authentication) {

		String email = authentication.getName();

		QrCode qr = qrService.generateQr(request, email);

		return ResponseEntity.ok(qr);
	}
}