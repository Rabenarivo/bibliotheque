package itu.biblio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"itu.biblio.controllers", "itu.biblio.services", "itu.biblio.repositories"})
public class BibliothequeApplication {
    public static void main(String[] args) {
        SpringApplication.run(BibliothequeApplication.class, args);
    }
}
