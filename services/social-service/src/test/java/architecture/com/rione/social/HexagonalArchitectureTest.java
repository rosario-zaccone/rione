package com.rione.social;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

class HexagonalArchitectureTest {

	private final JavaClasses classes = new ClassFileImporter()
		.withImportOption(new ImportOption.DoNotIncludeTests())
		.importPackages("com.rione.social");

	@Test
	void layersFollowHexagonalDependencyDirection() {
		layeredArchitecture()
			.consideringOnlyDependenciesInLayers()
			.layer("Domain").definedBy("..domain..")
			.layer("Application").definedBy("..application..")
			.layer("Infrastructure").definedBy("..infrastructure..")
			.whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
			.whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
			.whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
			.check(classes);
	}

	@Test
	void domainDoesNotDependOnFrameworksOrOuterLayers() {
		classes().that()
			.resideInAPackage("..domain..")
			.should()
			.onlyDependOnClassesThat()
			.resideInAnyPackage("java..", "com.rione.common.domain..", "com.rione.social.domain..")
			.check(classes);
	}

	@Test
	void applicationDoesNotDependOnInfrastructure() {
		classes().that()
			.resideInAPackage("..application..")
			.should()
			.onlyDependOnClassesThat()
			.resideInAnyPackage("java..", "org.springframework.stereotype..", "com.rione.common.application..",
					"com.rione.social.application..", "com.rione.social.domain..")
			.check(classes);
	}

	@Test
	void webAdaptersUseInputPortsInsteadOfOutputPortsOrPersistence() {
		classes().that()
			.resideInAPackage("..infrastructure.web..")
			.should()
			.onlyDependOnClassesThat()
			.resideOutsideOfPackages("com.rione.social.application.port.out..",
					"com.rione.social.infrastructure.persistence..")
			.check(classes);
	}
}
