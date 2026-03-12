package com.example.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jwt.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
