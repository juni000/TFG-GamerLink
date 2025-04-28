package com.app.web.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.app.web.service.CookieService;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtCookieUtil {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CookieService cookieService;

    private static final String JWT_COOKIE_NAME = "jwtToken";
    private static final int JWT_COOKIE_EXPIRATION = 86400;

    // Guardar token en cookie
    public void addTokenToCookie(Authentication authentication, HttpServletResponse response) {
        String token = jwtUtil.generateToken(authentication);
        cookieService.addHttpOnlyCookie(JWT_COOKIE_NAME, token, JWT_COOKIE_EXPIRATION, response);
    }

    // Eliminar cookie
    public void removeTokenCookie(HttpServletResponse response) {
        cookieService.deleteCookie(JWT_COOKIE_NAME, response);
    }

    // Obtener username desde el token (útil para validaciones)
    public String getUsernameFromToken(String token) {
        return jwtUtil.extractUserName(token);
    }
}
