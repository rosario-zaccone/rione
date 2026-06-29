package com.rione.notification.infrastructure.persistence;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
class NotificationSchemaMigrator implements ApplicationRunner {

	private static final String[] SUPPORTED_NOTIFICATION_TYPES = { "REQUEST_RECEIVED", "REQUEST_ACCEPTED",
			"POST_COMMENT_ADDED", "POST_REACTION_ADDED" };

	private final JdbcTemplate jdbcTemplate;

	NotificationSchemaMigrator(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void run(ApplicationArguments args) {
		jdbcTemplate.execute("alter table notifications drop constraint if exists notifications_type_check");
		jdbcTemplate.execute("""
				alter table notifications
				add constraint notifications_type_check
				check (type in ('REQUEST_RECEIVED', 'REQUEST_ACCEPTED', 'POST_COMMENT_ADDED', 'POST_REACTION_ADDED'))
				""");
	}

	static java.util.List<String> supportedNotificationTypes() {
		return java.util.List.of(SUPPORTED_NOTIFICATION_TYPES);
	}
}
