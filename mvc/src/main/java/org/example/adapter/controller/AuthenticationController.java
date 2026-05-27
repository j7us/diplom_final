package org.example.adapter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.adapter.controller.dto.AuthenticationResponseDto;
import org.example.adapter.controller.dto.LoginRequestDto;
import org.example.adapter.security.AuthenticationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Аутентификация", description = "эндпоинты логина и получения токенов")
@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Аутентификация пользователя")
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> authenticate(
            @RequestBody LoginRequestDto request) {
        log.info("Пришел запрос /login с параметрами: {}", request.getUsername());

        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @Operation(summary = "Получение нового access-токена")
    @PostMapping("/refresh_token")
    public ResponseEntity<AuthenticationResponseDto> refreshToken(HttpServletRequest request) {
        log.info("Пришел запрос /refresh_token с параметрами: {}", request.getHeader(HttpHeaders.AUTHORIZATION) != null);

        return ResponseEntity.ok(authenticationService.refreshToken(request.getHeader(HttpHeaders.AUTHORIZATION)));
    }
}
