package com.rione.notification.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class NotificationSchemaMigratorTest {

	@Test
	void refreshesNotificationTypeConstraintWithPostNotificationTypes() {
		RecordingJdbcTemplate jdbcTemplate = new RecordingJdbcTemplate();
		NotificationSchemaMigrator migrator = new NotificationSchemaMigrator(jdbcTemplate);

		migrator.run(null);

		assertThat(jdbcTemplate.executedSql()).hasSize(2);
		assertThat(jdbcTemplate.executedSql().get(0)).contains("drop constraint if exists notifications_type_check");
		assertThat(jdbcTemplate.executedSql().get(1))
			.contains("POST_COMMENT_ADDED")
			.contains("POST_REACTION_ADDED");
	}

	@Test
	void supportedTypesStayAlignedWithCurrentNotificationEnumValues() {
		assertThat(NotificationSchemaMigrator.supportedNotificationTypes())
			.containsExactly("REQUEST_RECEIVED", "REQUEST_ACCEPTED", "POST_COMMENT_ADDED", "POST_REACTION_ADDED");
	}

	private static final class RecordingJdbcTemplate extends JdbcTemplate {

		private final List<String> executedSql = new ArrayList<>();

		@Override
		public void execute(String sql) {
			executedSql.add(sql);
		}

		List<String> executedSql() {
			return executedSql;
		}
	}
}
