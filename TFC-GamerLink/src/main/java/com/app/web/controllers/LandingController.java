package com.app.web.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.web.entities.User;
import com.app.web.service.UserService;

@Controller
public class LandingController {
	private final UserService userService;

    public LandingController(UserService userService) {
        this.userService = userService;
    }
//	@GetMapping("/")
//	public String index(Model model, Principal principal) {
//		return "landing";
//	}
	
	
	@GetMapping("/landing")
	public String home(Model model, Principal principal) {        
        return "landing";
	}
}
