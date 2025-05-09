package com.app.web.service;

import java.time.LocalDateTime;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.web.dto.NewUserDto;
import com.app.web.entities.Role;
import com.app.web.entities.User;
import com.app.web.enums.RoleList;
import com.app.web.jwt.JwtUtil;
import com.app.web.repositories.RoleRepository;
import com.app.web.repositories.UserRepository;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

	private final UserService userService;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	public AuthService(UserService userService, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
			JwtUtil jwtUtil, AuthenticationManagerBuilder authenticationManagerBuilder) {
		this.userService = userService;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
		this.authenticationManagerBuilder = authenticationManagerBuilder;

	}

	public String authenticate(String username, String password, HttpServletResponse response) {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
				password);
		Authentication authResult = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authResult);
		System.out.println("Authentication successful for user: " + username);
		return jwtUtil.generateToken(authResult);
	}

	public void registerUser(NewUserDto newUserDto) {
		if (userService.existsByUserName(newUserDto.getUsername())) {
			throw new IllegalArgumentException("El nombre de usuario ya existe");
		}

		Role roleUser = roleRepository.findByName(RoleList.ROLE_USER)
				.orElseThrow(() -> new RuntimeException("Rol no encontrado"));
		User user = new User(newUserDto.getUsername(), passwordEncoder.encode(newUserDto.getPassword()), roleUser);
		userService.save(user);
	}
	
	public void logout(String token) {
        // 1. Invalidar el token
        jwtUtil.invalidateToken(token);
        
    }
	

}
