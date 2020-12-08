package com.loginregistration.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.loginregistration.model.Users;

@Controller
public class AppController {
	
	@GetMapping("/login")
	public String getLogin(Model model) {
		model.addAttribute("user", new Users());
		return "login";
	}
	
	@PostMapping("/login")
	public String postLogin(Users user) {
		return "redirect:/dashboard";
	}
	
	@GetMapping("/dashboard")
	public String dashboard() {
		return "dashboard";
	}
}
