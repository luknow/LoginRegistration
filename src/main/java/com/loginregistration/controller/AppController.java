package com.loginregistration.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.loginregistration.model.Users;

@Controller
public class AppController {
	
	@GetMapping("/login")
	public String getLogin(Model model) {
		model.addAttribute("user", new Users());
		return "login";
	}
}
