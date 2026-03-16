package com.example.jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jwt.entity.QrLogin;

public interface QrLoginRepository extends JpaRepository<QrLogin, Long> {

	Optional<QrLogin> findByQrToken(String qrToken);
}