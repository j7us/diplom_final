package org.example.service.security;

import lombok.RequiredArgsConstructor;

import org.example.dto.AuthenticationResponseDto;
import org.example.dto.LoginRequestDto;
import org.example.entity.Manager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponseDto authenticate(LoginRequestDto request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        Manager user = (Manager)userDetailsService.loadUserByUsername(request.getUsername());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthenticationResponseDto(accessToken, refreshToken);
    }

    public AuthenticationResponseDto refreshToken(String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Отсутствует значение header");
        }

        String token = authorizationHeader.substring(7);

        if (!jwtService.isValidRefresh(token)) {
            throw new RuntimeException("Ne valid token");
        }

        String username = jwtService.extractUsername(token);

        Manager user = (Manager)userDetailsService.loadUserByUsername(username);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthenticationResponseDto(accessToken, refreshToken);
    }
}
