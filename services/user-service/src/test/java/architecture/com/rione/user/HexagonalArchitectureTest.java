package com.rione.user;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.domain.JavaClasses;

class HexagonalArchitectureTest {

	private final JavaClasses classes = new ClassFileImporter()
		.withImportOption(new ImportOption.DoNotIncludeTests())
		.importPackages("com.rione.user");

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
		classes().that().resideInAPackage("..domain..")
			.should().onlyDependOnClassesThat()
			.resideInAnyPackage("java..", "com.rione.common.domain..", "com.rione.user.domain..")
			.check(classes);
	}

	@Test
	void applicationDoesNotDependOnInfrastructure() {
		classes().that().resideInAPackage("..application..")
			.should().onlyDependOnClassesThat()
			.resideInAnyPackage("java..", "com.rione.common.application..", "com.rione.user.application..",
					"com.rione.user.domain..")
			.check(classes);
	}

	@Test
	void webAdaptersUseInputPortsInsteadOfOutputPortsOrPersistence() {
		classes().that().resideInAPackage("..infrastructure.web..")
			.should().onlyDependOnClassesThat()
			.resideOutsideOfPackages("com.rione.user.application.port.out..", "com.rione.user.infrastructure.persistence..")
			.check(classes);
	}
}
