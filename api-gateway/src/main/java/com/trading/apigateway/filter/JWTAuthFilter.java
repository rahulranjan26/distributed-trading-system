package com.trading.apigateway.filter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.apigateway.utils.JWTUtils;

import com.trading.dto.ApiError;
import com.trading.dto.ApiResponse;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;



import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JWTAuthFilter implements GlobalFilter, Ordered {

    private final JWTUtils jwtUtils;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<String> PUBLIC_URLS = List.of(
            "/auth/register",
            "/auth/login"
    );

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (PUBLIC_URLS.stream().anyMatch(path::contains)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return handleUnauthorized(exchange, "Missing or invalid Authorization header");
        }

        try {
            String token = authHeader.split("Bearer ")[1];
            Long userId = jwtUtils.getUserId(token);
            String role = jwtUtils.getRole(token);

            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("X-User-Id", userId.toString())
                    .header("X-User-Role", role)
                    .build();
            log.info("Token: {}", token);
            log.info("UserId: {}", userId);
            log.info("Role: {}", role);
            log.info("Headers being sent: {}", request.getHeaders());

            return chain.filter(exchange.mutate().request(request).build());
        } catch (JwtException e) {
            return handleUnauthorized(exchange, "Invalid or expired token");
        }
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiError error = new ApiError(message, HttpStatus.UNAUTHORIZED);
        ApiResponse<?> apiResponse = new ApiResponse<>(error);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(apiResponse);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            return response.setComplete();
        }
    }
}