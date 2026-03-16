package com.example.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.jwt.entity.OtpLogin;

public interface OtpLoginRepository extends JpaRepository<OtpLogin, Long> {

	OtpLogin findTopByUserIdOrderByCreatedAtDesc(Long userId);

}