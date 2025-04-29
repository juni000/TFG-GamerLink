package com.app.web.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.web.entities.User;
import com.app.web.service.UserService;

@Controller
@RequestMapping("/")
public class HomeController {
	private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }
	
	@GetMapping("/home")
	public String home(Model model, Principal principal) {
        User currentUser = userService.getUserDetails();
        
        model.addAttribute("user", currentUser);
        model.addAttribute("userRole", currentUser.getRole().getName().toString());
        
        return "home";
	}
}
