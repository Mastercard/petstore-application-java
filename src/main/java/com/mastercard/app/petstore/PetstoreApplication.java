package com.mastercard.app.petstore;

import com.mastercard.app.petstore.examples.AdoptionFlowExample;
import com.mastercard.app.petstore.examples.EmployeeFlowExample;
import com.mastercard.app.petstore.examples.PetFlowExample;
import org.openapitools.client.ApiException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PetstoreApplication {

    public static void main(String[] args) {

        try {

            PetFlowExample petFlowExample = new PetFlowExample();
            petFlowExample.petUseCaseFlow();
            AdoptionFlowExample adoptionFlowExample = new AdoptionFlowExample();
            adoptionFlowExample.adoptionUseCase();
            EmployeeFlowExample employeeFlowExample = new EmployeeFlowExample();
            employeeFlowExample.employeeUseCase();
            SpringApplication.run(PetstoreApplication.class, args);
        } catch (ApiException e) {
            System.console().printf(e.getMessage());
        }
    }
}
