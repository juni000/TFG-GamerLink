package auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthControlador {
	
	private final AuthServicio authServicio = new AuthServicio();
	
	@PostMapping(value = "login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(authServicio.login(loginRequest));
	}
	 @PostMapping(value = "register")
	 public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
		 return ResponseEntity.ok(authServicio.register(registerRequest));
	 }	@PostMapping(value = "logout")
	public String logout() {
		return "logout";
	}
}
