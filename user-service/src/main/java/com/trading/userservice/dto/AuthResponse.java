package com.trading.userservice.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String refreshToken;
    private String accessToken;

    private String name;
    private String role;
}
