package com.egjarabo.ecommerce;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.egjarabo.ecommerce")
public class ArchitectureTest {

    // Controllers must not access repositories directly
    @ArchTest
    static final ArchRule controllers_should_not_access_repositories =
            noClasses()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().dependOnClassesThat()
                    .haveSimpleNameEndingWith("Repository");

    // Services must not depend on controllers
    @ArchTest
    static final ArchRule services_should_not_depend_on_controllers =
            noClasses()
                    .that().haveSimpleNameEndingWith("Service")
                    .should().dependOnClassesThat()
                    .haveSimpleNameEndingWith("Controller");

    // Controllers must not use JPA entities directly — only DTOs
    @ArchTest
    static final ArchRule controllers_should_not_use_entities =
            noClasses()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().dependOnClassesThat()
                    .areAnnotatedWith(jakarta.persistence.Entity.class);

    // Classes named Controller must be annotated with @RestController
    @ArchTest
    static final ArchRule controllers_must_be_annotated =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().beAnnotatedWith(RestController.class);
}