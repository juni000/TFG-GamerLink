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
				.csrf(csrf -> 
                csrf
                .disable())
            .authorizeHttpRequests(authRequest ->
              authRequest
                .requestMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
                )
            .sessionManagement(sessionManager->
                sessionManager 
                  .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(autenticationProvider)
            .addFilterBefore(jwtAutenticationFiltro, UsernamePasswordAuthenticationFilter.class)
            .build();
	}

	private Customizer<FormLoginConfigurer<HttpSecurity>> withDefaults() {
		return form -> form
				.loginPage("/auth/login")
				.defaultSuccessUrl("/usuarios", true)
				.failureUrl("/auth/login?error=true")
				.permitAll();
	}
}
