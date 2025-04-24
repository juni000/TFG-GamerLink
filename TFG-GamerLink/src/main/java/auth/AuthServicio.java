package auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.web.entidad.Roles;
import com.app.web.entidad.Usuario;
import com.app.web.repositorio.UsuarioIRepositorio;

import jwt.JwtServicio;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServicio {

    private final UsuarioIRepositorio usuariorepositorio;
    private final JwtServicio jwtServicio;
    private final AuthenticationManager authenticationManager;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Método de registro
    public AuthResponse register(RegisterRequest registerRequest) {
        // Codificar la contraseña antes de crear el usuario
        String contrasenaCifrada = encodePassword(registerRequest.getContrasena_hash());

        Usuario user = new Usuario(registerRequest.getNombre(), 
                                    registerRequest.getCorreo(),
                                    contrasenaCifrada,  // Aquí se guarda la contraseña cifrada
                                    registerRequest.getAvatar_url(), 
                                    registerRequest.getBiografia());
        System.out.println("Usuario a registrar: " + contrasenaCifrada);
        usuariorepositorio.save(user);  // Guardar el usuario con la contraseña cifrada
        return AuthResponse.builder().token(jwtServicio.getToken(user)).build();
    }

    // Método para codificar la contraseña
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);  // Cifra la contraseña con BCrypt
    }

    // Método de login
    public AuthResponse login(LoginRequest loginRequest) {
        // Realizar autenticación con el username y la contraseña proporcionada
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getNombre(), loginRequest.getContrasena_hash())
        );
        System.out.println("Usuario autenticado: " + loginRequest.getNombre());
        System.out.println("Contraseña: " + loginRequest.getContrasena_hash());
        // Buscar el usuario en la base de datos
        Usuario user = usuariorepositorio.findByNombre(loginRequest.getNombre()).orElseThrow();
        
        // Verificar si la contraseña proporcionada coincide con la guardada en la base de datos
        if (!passwordEncoder.matches(loginRequest.getContrasena_hash(), user.getContrasena_hash())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        
        // Si la contraseña es correcta, generar el JWT
        String token = jwtServicio.getToken(user);
        return AuthResponse.builder().token(token).build();
    }
}
