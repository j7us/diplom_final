package org.example.adapter.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AuthenticationResponseDto {

    private final String accessToken;
    private final String refreshToken;
}
