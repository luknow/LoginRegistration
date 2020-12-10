package com.loginregistration.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.loginregistration.model.UserAuthorities;
import com.loginregistration.model.Users;
import com.loginregistration.repository.UserAuthorityRepo;
import com.loginregistration.repository.UserRepo;

@Service
public class UserService {
		
	@Value("${userRole}")
	private String userRole;
		
	private UserRepo userRepo;
	private UserAuthorityRepo userAuthorityRepo;
	private PasswordEncoder passwordEncoder;
	
	public UserService(UserRepo userRepo, UserAuthorityRepo userAuthorityRepo, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.userAuthorityRepo = userAuthorityRepo;
		this.passwordEncoder = passwordEncoder;
	}

	public void add(Users user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setEnabled(true);
		UserAuthorities userAuthorities = new UserAuthorities();
		userAuthorities.setAuthority(userRole);
		user = userRepo.save(user);
		userAuthorities.setUserid(user.getId());
		userAuthorityRepo.save(userAuthorities);
	}

}
