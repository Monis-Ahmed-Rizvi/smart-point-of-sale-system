package com.smartpos.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfiguration {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth Service Routes
                .route("auth-service", r -> r.path("/api/auth/**")
                        .uri("http://localhost:8084"))
                
                // Inventory Service Routes
                .route("inventory-service", r -> r.path("/api/inventory/**")
                        .uri("http://localhost:8081"))
                
                // Transaction Service Routes
                .route("transaction-service", r -> r.path("/api/orders/**")
                        .uri("http://localhost:8082"))
                
                // Recipe Service Routes
                .route("recipe-service-products", r -> r.path("/api/products/**")
                        .uri("http://localhost:8083"))
                .route("recipe-service-recipes", r -> r.path("/api/recipes/**")
                        .uri("http://localhost:8083"))
                
                // Analytics Service Routes
                .route("analytics-service", r -> r.path("/api/analytics/**")
                        .uri("http://localhost:8085"))
                
                .build();
    }
}
