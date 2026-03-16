package com.example.jwt.dto;

public class ScanResponse {

	private String merchantName;
	private double amount;

	public ScanResponse(String merchantName, double amount) {
		this.merchantName = merchantName;
		this.amount = amount;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public double getAmount() {
		return amount;
	}
}