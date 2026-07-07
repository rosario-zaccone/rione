package com.rione.gateway;

import static org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions.circuitBreaker;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.web.servlet.function.RequestPredicates.path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
class GatewayRoutesConfiguration {

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	RouterFunction<ServerResponse> userServiceOpenApiRoute(
			@Value("${rione.gateway.routes.user-service-uri:http://localhost:8081}") String userServiceUri) {
		return route("user-service-openapi")
			.route(path("/users/v3/api-docs"), http())
			.before(stripPrefix(1))
			.before(uri(userServiceUri))
			.filter(circuitBreaker("user-service"))
			.build();
	}

	@Bean
	@Order(Ordered.LOWEST_PRECEDENCE)
	RouterFunction<ServerResponse> userServiceRoute(
			@Value("${rione.gateway.routes.user-service-uri:http://localhost:8081}") String userServiceUri) {
		return route("user-service")
			.route(path("/users/**"), http())
			.route(path("/cities"), http())
			.route(path("/cities/**"), http())
			.before(uri(userServiceUri))
			.filter(circuitBreaker("user-service"))
			.build();
	}

	@Bean
	RouterFunction<ServerResponse> socialServiceRootResourceRoute(
			@Value("${rione.gateway.routes.social-service-uri:http://localhost:8082}") String socialServiceUri) {
		return route("social-service-root-resources")
			.route(path("/neighbor-requests"), http())
			.route(path("/neighbor-requests/**"), http())
			.route(path("/me/**"), http())
			.before(uri(socialServiceUri))
			.filter(circuitBreaker("social-service"))
			.build();
	}

	@Bean
	RouterFunction<ServerResponse> socialServiceRoute(
			@Value("${rione.gateway.routes.social-service-uri:http://localhost:8082}") String socialServiceUri) {
		return route("social-service")
			.route(path("/social/**"), http())
			.before(stripPrefix(1))
			.before(uri(socialServiceUri))
			.filter(circuitBreaker("social-service"))
			.build();
	}

	@Bean
	RouterFunction<ServerResponse> notificationServiceRoute(
			@Value("${rione.gateway.routes.notification-service-uri:http://localhost:8084}") String notificationServiceUri) {
		return route("notification-service")
			.route(path("/notifications/**"), http())
			.before(uri(notificationServiceUri))
			.filter(circuitBreaker("notification-service"))
			.build();
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	RouterFunction<ServerResponse> postServiceOpenApiRoute(
			@Value("${rione.gateway.routes.post-service-uri:http://localhost:8083}") String postServiceUri) {
		return route("post-service-openapi")
			.route(path("/posts/v3/api-docs"), http())
			.before(stripPrefix(1))
			.before(uri(postServiceUri))
			.filter(circuitBreaker("post-service"))
			.build();
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	RouterFunction<ServerResponse> postServiceRoute(
			@Value("${rione.gateway.routes.post-service-uri:http://localhost:8083}") String postServiceUri) {
		return route("post-service")
			.route(path("/posts"), http())
			.route(path("/posts/**"), http())
			.route(path("/me/posts"), http())
			.route(path("/users/{authorId}/posts"), http())
			.route(path("/users/{authorId}/public-posts"), http())
			.before(uri(postServiceUri))
			.filter(circuitBreaker("post-service"))
			.build();
	}
}
