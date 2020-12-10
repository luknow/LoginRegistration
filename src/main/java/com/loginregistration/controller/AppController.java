package com.loginregistration.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.loginregistration.model.Users;
import com.loginregistration.repository.UserRepo;
import com.loginregistration.service.UserService;

@Controller
public class AppController {
	
	private UserRepo userRepo;
	private UserService userService;
	
	public AppController(UserRepo userRepo, UserService userService) {
		this.userRepo = userRepo;
		this.userService = userService;
	}
	
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
	
	@GetMapping("/admin")
	public String admin(RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("success", "Witaj adminie!");
		return "redirect:/dashboard";
	}
	
	@GetMapping("/denied")
	public String denied() {
		return "denied";
	}
	
	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("user", new Users());
		return "register";
	}
	
	@PostMapping("/signup")
	public String signup(@ModelAttribute("user") @Valid Users user, BindingResult bindingResult, Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("user", user);
			return "register";
		}else {
			userRepo.findUserByUsernameIgnoreCase(user.getUsername()).ifPresentOrElse(element -> 
				redirectAttributes.addFlashAttribute("danger", "Login zajęty!"), 
				() -> {userService.add(user); 
				redirectAttributes.addFlashAttribute("success", "Konto utworzono pomyślnie!");});
			model.addAttribute("user", new Users());
			return "redirect:/register";
		}
		
	}
}
