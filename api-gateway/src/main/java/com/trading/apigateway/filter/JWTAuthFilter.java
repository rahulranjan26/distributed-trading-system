package com.trading.apigateway.filter;


import com.trading.apigateway.utils.JWTUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;



import java.util.List;

@Configuration
@RequiredArgsConstructor
public class JWTAuthFilter implements GlobalFilter, Ordered {


    private final JWTUtils jwtUtils;
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
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.split("Bearer ")[1];
        Long userId = jwtUtils.getUserId(token);
        String role = jwtUtils.getRole(token);

        try {
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("X-User-Id", userId.toString())
                    .header("X-User-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(request).build());
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

    }
}
