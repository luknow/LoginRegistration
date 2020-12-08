package com.loginregistration.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.loginregistration.model.Users;


@Repository
public interface UserRepo extends JpaRepository<Users, Long>{
	
	Optional<Users> findUserByUsernameIgnoreCase(String username);
}
