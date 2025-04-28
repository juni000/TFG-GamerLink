package com.app.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.web.jwt.JwtCookieUtil;
import com.app.web.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private JwtCookieUtil jwtCookieUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletResponse response,
            Model model) {

        try {
            // 1. Autenticar
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            // 2. Guardar token en cookie (usando JwtCookieUtil)
            jwtCookieUtil.addTokenToCookie(authentication, response);

            // 3. Redirigir
            return "redirect:/home";

        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Credenciales inválidas");
            return "auth/login";
        }
    }

    // Cerrar sesión
    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        jwtCookieUtil.removeTokenCookie(response);
        return "redirect:/auth/login";
    }
}
