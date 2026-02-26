package org.example.service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import org.example.config.SecurityProp;
import org.example.entity.Manager;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final SecurityProp securityProp;

    private SecretKey getSgningKey() {

        byte[] keyBytes = Decoders.BASE64URL.decode(securityProp.getSecretKey());

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Manager user) {

        return generateToken(user, securityProp.getAccessTokenExpiration());
    }

    public String generateRefreshToken(Manager user) {

        return generateToken(user, securityProp.getRefreshTokenExpiration());
    }

    private String generateToken(Manager user, long expiryTime) {
        JwtBuilder builder = Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiryTime))
                .signWith(getSgningKey());

        return builder.compact();
    }

    private Claims extractAllClaims(String token) {

        JwtParserBuilder parser = Jwts.parser();
        parser.verifyWith(getSgningKey());

        return parser.build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {

        Claims claims = extractAllClaims(token);

        return resolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return !extractExpiration(token).before(new Date());
    }

    public boolean isValidAccess(String token) {
        return isTokenExpired(token);
    }

    public boolean isValidRefresh(String token) {
        return isTokenExpired(token);
    }
}
