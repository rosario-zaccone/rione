package com.rione.user.infrastructure.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader("Authorization");
		if (header == null || !header.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		try {
			AuthenticatedPrincipal principal = jwtService.parse(header.substring(7));
			List<SimpleGrantedAuthority> authorities = new ArrayList<>();
			if (principal.admin()) {
				authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
			}
			if (principal.service()) {
				authorities.add(new SimpleGrantedAuthority("ROLE_SERVICE"));
			}
			SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, authorities));
			filterChain.doFilter(request, response);
		}
		catch (AuthorizationException exception) {
			SecurityContextHolder.clearContext();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
		}
	}
}
