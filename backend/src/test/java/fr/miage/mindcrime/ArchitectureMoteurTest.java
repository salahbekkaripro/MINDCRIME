package fr.miage.mindcrime.moteur.generation;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureMoteurTest {

    @Test
    void moteurNeDependNiDeSpringNiDeJpaNiDeApi() {
        JavaClasses classes = new ClassFileImporter().importPackages("fr.miage.mindcrime.moteur");

        ArchRule regle = noClasses()
                .that().resideInAPackage("fr.miage.mindcrime.moteur..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "javax.persistence..",
                        "fr.miage.mindcrime.api.."
                );

        regle.check(classes);
    }
}