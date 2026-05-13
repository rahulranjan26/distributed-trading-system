package com.trading.userservice.service;


import com.trading.userservice.dto.AuthResponse;
import com.trading.userservice.dto.LoginUserRequest;
import com.trading.userservice.dto.RegisterUserRequest;
import com.trading.userservice.entity.User;
import com.trading.userservice.exceptions.DuplicateEmailException;
import com.trading.userservice.exceptions.InvalidCredentialsException;
import com.trading.userservice.exceptions.UserNotFoundException;
import com.trading.userservice.repository.UserRepository;
import com.trading.userservice.security.JWTUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JWTUtils jWTUtils;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public ResponseEntity<AuthResponse> register(RegisterUserRequest userRequest) {
        Optional<User> user = userRepository.findUserByEmail(userRequest.getEmail());
        if (user.isPresent())
            throw new DuplicateEmailException(userRequest.getEmail());
        User newUser = User.builder()
                .email(userRequest.getEmail())
                .name(userRequest.getName())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .role(userRequest.getRole())
                .build();

        User savedUser = userRepository.save(newUser);

        String accessToken = jWTUtils.generateToken(savedUser);
        String refreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                "refresh:" + savedUser.getUserId(),
                refreshToken,
                7,
                TimeUnit.DAYS
        );
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .name(savedUser.getName())
                .role(savedUser.getRole().toString())
                .build());
    }

    public ResponseEntity<AuthResponse> login(LoginUserRequest loginRequest) {
        User user = userRepository.findUserByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException(loginRequest.getEmail()));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
            throw new InvalidCredentialsException();

        String accessToken = jWTUtils.generateToken(user);
        String refreshToken = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set(
                "refresh:" + user.getUserId(),
                refreshToken,
                7,
                TimeUnit.DAYS
        );

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .name(user.getName())
                .role(user.getRole().toString())
                .build());
    }
}
