package com.app.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.web.dto.LoginUserDto;
import com.app.web.dto.NewUserDto;
import com.app.web.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/auth")
public class UserController {


	@Autowired
	private AuthService authService;
	
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("newUserDto", new NewUserDto()); // Nombre debe coincidir con th:object
		return "auth/register";
	}
	@PostMapping("/register")
	public String processRegister(@Valid @ModelAttribute("newUserDto") NewUserDto newUserDto, Model model) {
		try {
			authService.registerUser(newUserDto);
			return "redirect:/auth/login";
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", e.getMessage());
			return "auth/register";
		}
	}
	@GetMapping("/login")
	public String showLoginForm() {
		return "auth/login";
	}

	@PostMapping("/login")
	public String processLogin(@Valid @RequestBody LoginUserDto loginUserDto,
			HttpServletResponse response, Model model) {

		try {
			String roleName = authService.authenticate(loginUserDto.getUserName(), loginUserDto.getPassword(), response);

			return "redirect:/home";

		} catch (BadCredentialsException e) {
			model.addAttribute("error", "Credenciales inválidas");
			return "auth/login";
		}
	}

}
