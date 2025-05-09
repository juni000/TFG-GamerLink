package com.app.web.controllers;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.web.dto.LoginUserDto;
import com.app.web.dto.NewUserDto;
import com.app.web.service.AuthService;
import com.app.web.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/auth")
public class UserController {


	@Autowired
	private AuthService authService;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("newUserDto", new NewUserDto()); // Nombre debe coincidir con th:object
		return "auth/register";
	}
	@PostMapping("/register")
	public String processRegister(@Valid @ModelAttribute("newUserDto") NewUserDto newUserDto, 
	                           BindingResult bindingResult, 
	                           Model model) {
	    
	    // Validación manual del nombre de usuario
	    if (userService.existsByUserName(newUserDto.getUsername())) {
	        bindingResult.rejectValue("username", "error.username", "El nombre de usuario ya existe");
	    }else if(newUserDto.getPassword().length() < 8) {
	    	bindingResult.rejectValue("password", "error.password", "La contraseña debe tener al menos 8 caracteres");
	    }else if(!newUserDto.getPassword().equals(newUserDto.getConfirmPassword())) {
	    	bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Las contraseñas no coinciden");
	    }
	    
	    if (bindingResult.hasErrors()) {
	        return "auth/register"; // Devuelve la vista con los errores
	    }
	    
	    authService.registerUser(newUserDto);
	    return "redirect:/auth/login?registerSuccess=true";
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
	
	@PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = extractToken(request);
        authService.logout(token);
        
        return ResponseEntity.ok().body(Map.of(
            "message", "Logout exitoso",
            "timestamp", LocalDateTime.now()
        ));
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token inválido");
        }
        return header.substring(7);
    }
}
