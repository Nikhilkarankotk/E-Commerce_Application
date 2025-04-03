//package com.nkk.gatewayServer.filter;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//import java.util.Arrays;
//import java.util.List;
//@Component
//public class RoleBasedAuthFilter extends AbstractGatewayFilterFactory<RoleBasedAuthFilter.Config> {
//    public RoleBasedAuthFilter() {
//        super(Config.class);
//    }
//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> {
//            // Get user roles from the request headers
//            String rolesHeader = exchange.getRequest().getHeaders().getFirst("X-User-Roles");
//            if (rolesHeader == null) {
//                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
//                return exchange.getResponse().setComplete();
//            }
//            List<String> roles = Arrays.asList(rolesHeader.split(","));
//            // Check if the user has the required role
//            if (!roles.contains(config.getRequiredRole())) {
//                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
//                return exchange.getResponse().setComplete();
//            }
//            // Proceed with the request
//            return chain.filter(exchange);
//        };
//    }
//    public static class Config {
//        private String requiredRole;
//        public String getRequiredRole() {
//            return requiredRole;
//        }
//        public void setRequiredRole(String requiredRole) {
//            this.requiredRole = requiredRole;
//        }
//    }
//}
