package com.trading.userservice.dto;


import com.trading.userservice.enums.Role;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest {
    private String name;
    private String email;
    private String password;
    private Role role;
}
