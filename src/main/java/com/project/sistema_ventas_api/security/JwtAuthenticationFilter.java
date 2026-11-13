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
        throws ServletException, IOException
{

    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String username;

    // Verificamos si la petición no trae un token valido en el encabezado
    if (authHeader == null || !authHeader.startsWith("Bearer "))
    {
        filterChain.doFilter(request, response);
        return;
    }

    // Si es que  trae un token valido, se recorta "Bearer " para obtener el token puro
    jwt = authHeader.substring(7);
    username = jwtUtil.obtenerUsername(jwt); //Extraemos el username

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null)
    {
        // Buscar al usuario en la base de datos
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

        // Validar firma  y tiempo de expiración
        if (jwtUtil.tokenValido(jwt, userDetails.getUsername())) //Si el token es valido
        {
            //Extraemos el ID de la tienda del token
            String tiendaId = jwtUtil.obtenerTiendaID(jwt);

            //Lo inyectamos en la memoria exclusiva de este hilo
            TenantContext.setCurrentTenant(tiendaId);

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

    try {
        filterChain.doFilter(request, response);
    } finally {

        TenantContext.clear();
    }
}


}
