package com.mastercard.app.petstore;

import com.mastercard.app.petstore.services.CatService;
import com.mastercard.app.petstore.services.PetService;
import com.mastercard.app.petstore.utils.MockDataBuilders;
import com.mastercard.app.petstore.utils.OAuth2Utils;
import org.openapitools.client.ApiClient;
import org.openapitools.client.api.CatsApi;
import org.openapitools.client.api.PetsApi;
import org.openapitools.client.model.Cat;
import org.openapitools.client.model.NewCat;
import org.openapitools.client.model.PetStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

@SpringBootApplication
@ComponentScan(basePackages = {"com.mastercard.app.petstore.utils"})
@Import({com.mastercard.app.petstore.utils.OAuth2Utils.class})
public class PetstoreApplication {

    private CatService catService;
    private PetService petService;

    @Autowired
    private OAuth2Utils oauth2Utils;

    @Bean
    public ApiClient apiClient() {
        return oauth2Utils.apiClient();
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx, ApiClient apiClient)  {

        return args -> {
            catService = new CatService(new CatsApi(apiClient));
            petService = new PetService(new PetsApi(apiClient));
            NewCat newCat = MockDataBuilders.buildNewCat();
            Cat cat = catService.addCat(newCat);
            cat = catService.getCat(cat.getId().toString());

            //Update cats name
            cat.setName("Viena");
            catService.updateCat(cat, "0");

            //Set pet status
            PetStatus status = new PetStatus().value("RESERVED");
            petService.updatePetStatus(cat.getId(), status,  "1");

            System.out.println(cat.getName());
            //Remove pet
            petService.removePet(cat.getId());
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(PetstoreApplication.class, args);
    }
}
