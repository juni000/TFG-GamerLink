package configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import jwt.JwtFiltroAutentificacion;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ConfiguracionSeguridad {
	
	private final JwtFiltroAutentificacion jwtAutenticationFiltro;
    private final AuthenticationProvider autenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())  // Desactivar CSRF para APIs REST
                .authorizeHttpRequests(authRequest -> authRequest
                	    .requestMatchers("/auth/**", "/login_usuario", "/css/**", "/js/**", "/images/**").permitAll()  // Permite acceso sin autenticación a rutas de login y recursos estáticos
                	    .anyRequest().authenticated()
                	    )
                .formLogin(form -> form
                    .loginPage("/login_usuario")  // Usar tu página de login personalizada
                    .defaultSuccessUrl("/perfil", true)  // Redirigir a /perfil después de un login exitoso
                    .permitAll()  // Permitir acceso sin autenticación
                    .failureUrl("/login_usuario?error=true")  // Redirigir si las credenciales son incorrectas
                )
                .sessionManagement(sessionManager -> sessionManager
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // No usar sesiones (estamos trabajando con JWT)
                .authenticationProvider(autenticationProvider)  // Proveedor de autenticación personalizado
                .addFilterBefore(jwtAutenticationFiltro, UsernamePasswordAuthenticationFilter.class)  // Agregar el filtro JWT antes del filtro de autenticación por defecto
                .exceptionHandling(exceptionHandling -> exceptionHandling
                    .authenticationEntryPoint((request, response, authException) -> {
                        // Si la autenticación falla, no redirigir a login, sino responder con un error 401
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado");
                    })
                )
                .build();
    }
}
