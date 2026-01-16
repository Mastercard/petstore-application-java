package com.mastercard.app.petstore.examples;

import com.mastercard.app.petstore.services.CatService;
import com.mastercard.app.petstore.services.PetService;
import com.mastercard.app.petstore.utils.MockDataBuilders;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.Cat;
import org.openapitools.client.model.NewCat;
import org.openapitools.client.model.PetStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component("PetFlowExample")
public class PetFlowExample {

    private CatService catService;
    private PetService petService;
    /**
     * Constructs a new {@code PetFlowExample} with the required service dependencies.
     * <p>
     * This constructor is annotated with {@link org.springframework.beans.factory.annotation.Autowired},
     * allowing Spring to automatically inject the {@link CatService} and {@link PetService} beans
     * at runtime. These services are then used to perform operations related to cats and pets
     * within the flow example.
     * </p>
     *
     * @param catService the service responsible for managing cat-related operations
     * @param petService the service responsible for managing general pet-related operations
     */
    @Autowired
    public PetFlowExample(CatService catService, PetService petService){
        this.catService = catService;
        this.petService=petService;
    }
    /**
     * Pet use case flow. Shows creating a cat, updating their status and then removing them
     *
     * @throws ApiException thrown whenever there is an issue sending a request
     */
    public void petUseCaseFlow() throws ApiException {
        //Add pet
        NewCat newCat = MockDataBuilders.buildNewCat();
        Cat cat = catService.addCat(newCat);
        cat = catService.getCat(cat.getId().toString());

        //Update cats name
        cat.setName("Catso");
        catService.updateCat(cat, "0");

        //Set pet status
        PetStatus status = new PetStatus().value("RESERVED");
        petService.updatePetStatus(cat.getId(), status,  "1");

        //Remove pet
        petService.removePet(cat.getId());
    }
}
