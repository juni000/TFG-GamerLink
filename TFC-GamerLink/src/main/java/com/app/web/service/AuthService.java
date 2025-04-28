package com.app.web.service;

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

import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final CookieService cookieService;
    static final String JWT_COOKIE_NAME = "jwt";
    static final int JWT_COOKIE_MAX_AGE = 7 * 24 * 60 * 60; // 7 days in seconds
    
    public AuthService(UserService userService, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManagerBuilder authenticationManagerBuilder, CookieService cookieService) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.cookieService = cookieService;
    }


    public String authenticate(String username, String password, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authResult = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authResult);

        String jwt = jwtUtil.generateToken(authResult);
        cookieService.addHttpOnlyCookie(JWT_COOKIE_NAME, jwt, JWT_COOKIE_MAX_AGE, response);

        User user = userService.findByUserName(username);

        return user.getRole().getName().toString();
    }

    public void registerUser(NewUserDto newUserDto) {
        if (userService.existsByUserName(newUserDto.getUserName())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        Role roleUser = roleRepository.findByName(RoleList.ROLE_USER).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        User user = new User(newUserDto.getUserName(), passwordEncoder.encode(newUserDto.getPassword()), roleUser);
        userService.save(user);
    }

    public void logout(HttpServletResponse response){
        cookieService.deleteCookie("jwt",response);
    }
}
