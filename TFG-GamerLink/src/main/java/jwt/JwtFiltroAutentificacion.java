package jwt;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFiltroAutentificacion extends OncePerRequestFilter {

    private final JwtServicio jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException, java.io.IOException {

        final String token = getTokenFromRequest(request);

        if (token == null) {
            filterChain.doFilter(request, response);  // No se encuentra el token, pasa al siguiente filtro
            return;
        }

        try {
            String username = jwtService.getUsernameFromToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Cargar detalles del usuario desde el servicio de UserDetails
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Verificar si el token es válido
                if (jwtService.isTokenValid(token, userDetails)) {
                    // Crear el token de autenticación
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Establecer la autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    // Log para verificar la autenticación
                    System.out.println("Usuario autenticado: " + username);
                }
            }
        } catch (JwtException e) {
            // Excepciones específicas de JWT, podría ser útil para manejar errores de token
            System.out.println("Error al procesar el JWT: " + e.getMessage());
        } catch (Exception e) {
            // Captura cualquier otro error inesperado
            System.out.println("Error general al procesar el JWT: " + e.getMessage());
        }

        // Pasa al siguiente filtro en la cadena
        filterChain.doFilter(request, response);
    }

    // Método para extraer el token del encabezado de autorización
    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);  // Elimina el prefijo "Bearer " y retorna el token
        }
        return null;  // No se encuentra el token
    }
}
