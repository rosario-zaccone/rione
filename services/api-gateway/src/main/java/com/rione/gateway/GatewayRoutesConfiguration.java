package com.rione.gateway;

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
			.build();
	}

	@Bean
	@Order(Ordered.LOWEST_PRECEDENCE)
	RouterFunction<ServerResponse> userServiceRoute(
			@Value("${rione.gateway.routes.user-service-uri:http://localhost:8081}") String userServiceUri) {
		return route("user-service")
			.route(path("/users/**"), http())
			.before(uri(userServiceUri))
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
			.build();
	}

	@Bean
	RouterFunction<ServerResponse> socialServiceRoute(
			@Value("${rione.gateway.routes.social-service-uri:http://localhost:8082}") String socialServiceUri) {
		return route("social-service")
			.route(path("/social/**"), http())
			.before(stripPrefix(1))
			.before(uri(socialServiceUri))
			.build();
	}

	@Bean
	RouterFunction<ServerResponse> notificationServiceRoute(
			@Value("${rione.gateway.routes.notification-service-uri:http://localhost:8084}") String notificationServiceUri) {
		return route("notification-service")
			.route(path("/notifications/**"), http())
			.before(uri(notificationServiceUri))
			.build();
	}
}
