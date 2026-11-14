package com.project.sistema_ventas_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }


    @Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
    try {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Verificamos si la petición no trae un token valido en el encabezado
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Si trae un token valido, recortamos "Bearer "
        jwt = authHeader.substring(7);
        username = jwtUtil.obtenerUsername(jwt);

        // Extraemos el id de la tienda y lo almacenamos para las consultas
        String tiendaId = jwtUtil.obtenerTiendaID(jwt);
        TenantContext.setCurrentTenant(tiendaId);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Validar firma y tiempo de expiracion
            if (jwtUtil.tokenValido(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Registrar al usuario para esta unica peticion
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    } finally {
        TenantContext.clear();
    }
}


}
