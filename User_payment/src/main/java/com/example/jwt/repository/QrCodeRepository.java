package com.example.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jwt.entity.QrCode;

public interface QrCodeRepository extends JpaRepository<QrCode, Long> {
}