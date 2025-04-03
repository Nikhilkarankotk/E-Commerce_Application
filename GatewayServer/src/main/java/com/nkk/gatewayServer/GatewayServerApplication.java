package com.nkk.gatewayServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServerApplication.class, args);
	}

	@Bean
	public RouteLocator ecommereceRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
		return routeLocatorBuilder.routes()
				.route(p-> p
						.path("/categories/**")
						.filters(f -> f.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
						.uri("lb://PRODUCTS"))
				.route(p-> p
						.path("/products/**")
						.filters(f -> f.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
						.uri("lb://PRODUCTS"))
				.route(p-> p
						.path("/cart/**")
						.filters(f -> f.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
						.uri("lb://CARTS"))
				.route(p-> p
						.path("/orders/**")
						.filters(f -> f.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
						.uri("lb://ORDERS"))
				.route(p-> p
						.path("/shipping/**")
						.filters(f -> f.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
						.uri("lb://SHIPPINGS"))
				.route(p-> p
						.path("/payments/**")
						.filters(f -> f.addResponseHeader("X-Response-Time",LocalDateTime.now().toString()))
						.uri("lb://PAYMENTS"))
				.build();
	}
}
