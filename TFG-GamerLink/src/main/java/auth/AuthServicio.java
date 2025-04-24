package auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.web.entidad.Usuario;
import com.app.web.repositorio.UsuarioIRepositorio;

import jwt.JwtServicio;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServicio {

	private UsuarioIRepositorio usuariorepositorio;

	private final JwtServicio jwtServicio;
	private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private final AuthenticationManager authenticationManager;

	public AuthResponse login(LoginRequest loginRequest) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getNombre(), loginRequest.getContrasena_hash()));
		Usuario user = usuariorepositorio.findByNombre(loginRequest.getNombre()).orElseThrow();
		String token = jwtServicio.getToken(user);
		return AuthResponse.builder().token(token).build();
	}

	public AuthResponse register(RegisterRequest registerRequest) {
		Usuario user = new Usuario(registerRequest.getNombre(), registerRequest.getCorreo(),
				encodePassword(registerRequest.getContrasena_hash()), registerRequest.getAvatar_url(), registerRequest.getBiografia());
		usuariorepositorio.save(user);
		return AuthResponse.builder().token(jwtServicio.getToken(user)).build();
	}
	public String logout() {
		return "logout";
	}
	//haz un metodo para la contraseña
	public String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

}
