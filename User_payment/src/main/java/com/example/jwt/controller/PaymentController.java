package com.example.jwt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.jwt.dto.PaymentRequest;
import com.example.jwt.dto.ScanRequest;
import com.example.jwt.dto.ScanResponse;
import com.example.jwt.service.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // SCAN QR
    @PostMapping("/scan")
    public ScanResponse scanQr(@RequestBody ScanRequest request) {

        return paymentService.scanQr(request.getQrId());
    }

    // PAY USING QR
    @PostMapping("/pay")
    public String pay(@RequestBody PaymentRequest request,
                      Authentication authentication) {

        return paymentService.pay(request, authentication);
    }
}