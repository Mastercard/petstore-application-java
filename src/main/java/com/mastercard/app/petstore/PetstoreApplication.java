package com.mastercard.app.petstore;

import com.mastercard.app.petstore.examples.AdoptionFlowExample;
import com.mastercard.app.petstore.examples.EmployeeFlowExample;
import com.mastercard.app.petstore.examples.PetFlowExample;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.Adoption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PetstoreApplication {
    @Autowired
    PetFlowExample petFlowExample;
    @Autowired
    AdoptionFlowExample adoptionFlowExample;
    @Autowired
    EmployeeFlowExample employeeFlowExample;

    @Bean
    PetFlowExample petFlowExample() throws ApiException {
        petFlowExample = new PetFlowExample();
        return petFlowExample;
    }

    @Bean
    AdoptionFlowExample adoptionFlowExample() {
        adoptionFlowExample = new AdoptionFlowExample();
        return adoptionFlowExample;
    }

    @Bean
    EmployeeFlowExample employeeFlowExample() {
        employeeFlowExample = new EmployeeFlowExample();
        return employeeFlowExample;
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx)  {

        return args -> {
            petFlowExample.petUseCaseFlow();
            adoptionFlowExample.adoptionUseCase();
            employeeFlowExample.employeeUseCase();
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(PetstoreApplication.class, args);
    }
}
