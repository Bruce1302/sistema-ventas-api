package com.project.sistema_ventas_api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // La puerta de entrada libre

                        //Reglas operativas
                        .requestMatchers(HttpMethod.POST, "/ventas/**").hasAnyRole("ADMIN", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/ventas/**").hasAnyRole("ADMIN", "CAJERO")

                        //Operaciones administrativas
                        .requestMatchers(HttpMethod.POST, "/productos/**", "/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/productos/**", "/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/productos/**", "/categorias/**").hasRole("ADMIN")

                        //Ambos pueden ver los proudctos y categorias
                        .requestMatchers(HttpMethod.GET, "/productos/**", "/categorias/**").hasAnyRole("ADMIN", "CAJERO")

                        //Solo admin controla los usuarios
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")

                        .anyRequest().authenticated()

                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
