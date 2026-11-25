package com.project.sistema_ventas_api.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(AuthenticationProvider authenticationProvider, JwtAuthenticationFilter jwtAuthFilter)
    {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Value("${cors.allowed-origin}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http
                .cors(org.springframework.security.config.Customizer.withDefaults()) // activamos el cors

                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Evita bloqueos de preflight CORS
                        .requestMatchers("/auth/**").permitAll() // La puerta de entrada libre

                        //Reglas operativas
                        .requestMatchers(HttpMethod.POST, "/ventas/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/ventas/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "CAJERO")
                        .requestMatchers(HttpMethod.POST, "/productos/**", "/categorias/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "CAJERO")

                        //Operaciones administrativas
                        .requestMatchers(HttpMethod.PUT,"/productos/**", "/categorias/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "CAJERO")
                        .requestMatchers(HttpMethod.DELETE,"/productos/**", "/categorias/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "CAJERO")

                        //Ambos pueden ver los productos, categorias y usuarios
                        .requestMatchers(HttpMethod.GET, "/productos/**", "/categorias/**", "/tiendas/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "CAJERO")
                        .requestMatchers("/usuarios/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "CAJERO")

                        //Solo el super admin controla las tiendas
                        .requestMatchers("/tiendas/**").hasRole("SUPER_ADMIN")

                        // Cierre de seguridad por defecto para cualquier endpoint futuro
                        .anyRequest().authenticated()

                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permite conectarse a varios frontend
        List<String> origenes = Arrays.stream(frontendUrl.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();

        configuration.setAllowedOrigins(origenes);
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Idempotency-Key"));
        configuration.setExposedHeaders(List.of("Content-Disposition"));
        configuration.setMaxAge(3600L);


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
