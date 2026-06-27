package com.medisalud.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.medisalud", importOptions = ImportOption.DoNotIncludeTests.class)
public class CleanArchitectureTest {

    @ArchTest
    static final ArchRule domain_should_not_depend_on_infrastructure =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..", "..presentation..")
                    .because("Domain layer must be independent of external concerns (Infrastructure/Presentation).");

    @ArchTest
    static final ArchRule application_should_not_depend_on_infrastructure =
            noClasses()
                    .that().resideInAPackage("..application..")
                    .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..", "..presentation..")
                    .because("Application layer must only orchestrate domain logic and depend on domain/shared, not infrastructure.");

    @ArchTest
    static final ArchRule domain_should_not_use_spring_framework =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("org.springframework..")
                    .because("Domain layer must be pure Java and not depend on Spring Framework annotations or classes.");
}
