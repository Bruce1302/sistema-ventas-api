package com.project.sistema_ventas_api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("jwt.secret.key")
    private String secretKey;

    @Value("jwt.expiration")
    private String expirationTime;

    private SecretKey getSigningKey()
    {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    //Creamos el token y le pasamos los datos del usuario
    public String generarToken(String username, String rol)
    {
        return Jwts.builder()
                .setSubject(username)
                .claim("rol", rol)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    //Extraer el dueño del token
    public String obtenerUsername(String token)
    {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    //Verificamos validez del token
    public boolean tokenValido(String token, String username)
    {
        return obtenerUsername(token).equals(username) && !tokenExpirado(token);
    }

    public boolean tokenExpirado(String token)
    {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }


}
