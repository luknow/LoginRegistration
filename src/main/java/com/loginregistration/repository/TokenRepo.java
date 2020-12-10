package com.loginregistration.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loginregistration.model.Token;

public interface TokenRepo  extends JpaRepository<Token, Long>{

	Optional<Token> findByValue(String value);
}
