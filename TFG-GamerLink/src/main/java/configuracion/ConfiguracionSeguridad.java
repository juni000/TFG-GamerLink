package configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ConfiguracionSeguridad {
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
	            .formLogin(withDefaults())
	            .logout(logout -> logout
	                .logoutUrl("/auth/logout")
	                .logoutSuccessUrl("/auth/login?logout=true")
	                .permitAll())	            
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
