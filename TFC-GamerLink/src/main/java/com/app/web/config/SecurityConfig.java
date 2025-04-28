package com.app.web.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.app.web.jwt.JwtAuthenticationFilter;
import com.app.web.jwt.JwtEntryPoint;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	http
        .cors(Customizer.withDefaults())  // CORS
        .csrf(AbstractHttpConfigurer::disable)  // CSRF deshabilitado (APIs JWT)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**", "/static/**").permitAll()  // Rutas públicas
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/auth/login")  // Página de login Thymeleaf
            .defaultSuccessUrl("/home", true)
        )
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(jwtEntryPoint())  // Manejo de errores JWT
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Sin sesiones
        )
        .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtTokenFilter(){
        return new JwtAuthenticationFilter();
    }

    @Bean
    public JwtEntryPoint jwtEntryPoint(){
        return new JwtEntryPoint();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}