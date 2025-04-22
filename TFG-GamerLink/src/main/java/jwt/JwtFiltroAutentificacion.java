package jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFiltroAutentificacion extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String token = getTokenFromRequest(request);
	}

	private String getTokenFromRequest(HttpServletRequest request) {
		final String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
			return bearer.substring(7);
		}
		return null;
	}

}
