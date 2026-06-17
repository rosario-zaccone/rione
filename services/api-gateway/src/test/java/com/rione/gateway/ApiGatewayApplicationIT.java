package com.rione.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = { "rione.gateway.routes.user-service-uri=http://localhost:8081",
		"rione.gateway.routes.social-service-uri=http://localhost:8082" })
class ApiGatewayApplicationIT {

	@Test
	void contextLoads() {
	}
}
