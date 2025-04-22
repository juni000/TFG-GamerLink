package auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.web.entidad.Roles;
import com.app.web.entidad.Usuario;
import com.app.web.repositorio.RoleIRepositorio;
import com.app.web.repositorio.UsuarioIRepositorio;

import jwt.JwtServicio;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServicio {
	
	@Autowired
	private UsuarioIRepositorio Usuariorepositorio;
	
	@Autowired
	private RoleIRepositorio rolrepositorio;
	
	private final JwtServicio jwtServicio;
	public AuthResponse login(LoginRequest loginRequest) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	public AuthResponse register(RegisterRequest registerRequest) {
		Usuario user = new Usuario(registerRequest.getNombre(), registerRequest.getCorreo(),
				registerRequest.getContrasena_hash(),registerRequest.getAvatar_url(),
				registerRequest.getBiografia());
		Usuariorepositorio.save(user);
		return AuthResponse.builder()
				.token(jwtServicio.getToken(user)).build();
	}



}
