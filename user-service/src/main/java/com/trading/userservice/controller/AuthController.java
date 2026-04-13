package com.trading.userservice.controller;


import com.trading.userservice.dto.AuthResponse;
import com.trading.userservice.dto.LoginUserRequest;
import com.trading.userservice.dto.RegisterUserRequest;
import com.trading.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterUserRequest request) throws Exception {
        log.info("Registering the user with email id: {}", request.getEmail());
        return authService.register(request);
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody  LoginUserRequest loginUserRequest) throws Exception {
        log.info("Loggin the user with email: {}", loginUserRequest.getEmail());
        return authService.login(loginUserRequest);
    }

}
