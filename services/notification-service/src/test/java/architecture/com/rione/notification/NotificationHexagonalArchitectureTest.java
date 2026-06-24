package com.rione.notification;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

class NotificationHexagonalArchitectureTest {

	private final JavaClasses classes = new ClassFileImporter()
		.withImportOption(new ImportOption.DoNotIncludeTests())
		.importPackages("com.rione.notification");

	@Test
	void domainDoesNotDependOnApplicationInfrastructureOrFrameworks() {
		noClasses().that()
			.resideInAPackage("..domain..")
			.should()
			.dependOnClassesThat()
			.resideInAnyPackage("..application..", "..infrastructure..", "org.springframework..", "jakarta.persistence..")
			.check(classes);
	}

	@Test
	void applicationDoesNotDependOnInfrastructure() {
		noClasses().that()
			.resideInAPackage("..application..")
			.should()
			.dependOnClassesThat()
			.resideInAPackage("..infrastructure..")
			.check(classes);
	}

	@Test
	void adaptersStayInInfrastructurePackage() {
		classes().that()
			.resideInAPackage("..infrastructure..")
			.should()
			.resideInAnyPackage("..infrastructure.config..", "..infrastructure.messaging..", "..infrastructure.persistence..",
					"..infrastructure.web..")
			.check(classes);
	}
}
