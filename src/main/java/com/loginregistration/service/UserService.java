package com.loginregistration.service;

import java.util.UUID;
import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.loginregistration.model.Token;
import com.loginregistration.model.UserAuthorities;
import com.loginregistration.model.Users;
import com.loginregistration.repository.TokenRepo;
import com.loginregistration.repository.UserAuthorityRepo;
import com.loginregistration.repository.UserRepo;

@Service
public class UserService {
	
	
	@Value("${appAddress}")
	private String appAddress;
	
	@Value("${userRole}")
	private String userRole;
	
	@Value("${mailSubject}")
	private String mailSubject;
	
	private UserRepo userRepo;
	private UserAuthorityRepo userAuthorityRepo;
	private TokenRepo tokenRepo;
	private MailService mailService;
	private PasswordEncoder passwordEncoder;
	
	public UserService(UserRepo userRepo, UserAuthorityRepo userAuthorityRepo, 
					   TokenRepo tokenRepo, MailService mailService, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.userAuthorityRepo = userAuthorityRepo;
		this.tokenRepo = tokenRepo;
		this.mailService = mailService;
		this.passwordEncoder = passwordEncoder;
	}

	public void add(Users user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setEnabled(false);
		UserAuthorities userAuthorities = new UserAuthorities();
		userAuthorities.setAuthority(userRole);
		user = userRepo.save(user);
		userAuthorities.setUserid(user.getId());
		userAuthorityRepo.save(userAuthorities);
		String tokenValue = createToken();
		saveToken(tokenValue, user);
		sendToken(tokenValue, user.getEmail());
	}

	private String createToken() {
		return UUID.randomUUID().toString();
		
	}
	
	private void saveToken(String tokenValue, Users user) {
		Token token = new Token();
		token.setValue(tokenValue);
		token.setUser(user);
		tokenRepo.save(token);
	}
	
	private void sendToken(String tokenValue, String email)  {
		String url = appAddress+"/token?value="+tokenValue;
		try {
			mailService.sendMail(email, mailSubject, url);
		} catch (MessagingException e) {
			System.out.println("Klasa "+this.getClass().getSimpleName());
			System.out.println("Metoda sendToken");
			e.printStackTrace();
		}
	}

}
