package com.trading.userservice.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserRequest {

    private String email;
    private String password;
}
