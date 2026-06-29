package com.rione.user.infrastructure.web;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
class ActiveUserTracker {

	private static final Duration ACTIVE_WINDOW = Duration.ofMinutes(5);

	private final Map<Long, Instant> lastSeen = new ConcurrentHashMap<>();

	void record(Long userId) {
		if (userId != null) {
			lastSeen.put(userId, Instant.now());
		}
	}

	long activeUsers() {
		Instant cutoff = Instant.now().minus(ACTIVE_WINDOW);
		lastSeen.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));
		return lastSeen.values().stream().filter(lastSeenAt -> !lastSeenAt.isBefore(cutoff)).count();
	}
}
