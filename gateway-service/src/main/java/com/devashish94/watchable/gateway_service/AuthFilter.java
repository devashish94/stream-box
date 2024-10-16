package com.devashish94.watchable.gateway_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final String AUTH_SERVICE_VALIDATE_URL = "http://node-auth-service/auth/validate";

    AuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            System.out.println("Headers: " + exchange.getRequest().getHeaders());

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                System.out.println("invalid auth header format");
                return exchange.getResponse().setComplete();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, authHeader);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            return Mono.fromCallable(() -> restTemplate.exchange(AUTH_SERVICE_VALIDATE_URL, HttpMethod.GET, entity, String.class))
                    .subscribeOn(Schedulers.boundedElastic()) // Use boundedElastic for blocking calls
                    .flatMap(response -> {
                        System.out.println("Auth success, continue...");
                        System.out.println("Response: " + response.getBody());
                        String userId = Objects.requireNonNull(response.getBody()).split(" ")[1];
                        System.out.println("userId: " + userId);
                        ServerHttpRequest request = exchange.getRequest().mutate()
                                .header("X-USER-ID", userId)
                                .build();

                        // Forward the request to user-service
                        return chain.filter(exchange.mutate().request(request).build());
                    })
                    .onErrorResume(e -> {
                        System.out.println("Error during authentication: " + e.getMessage());
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    });
        });
    }

    public static class Config {
    }

}
