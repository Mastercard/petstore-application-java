package com.mastercard.app.petstore.examples;

import com.mastercard.app.petstore.services.CatService;
import com.mastercard.app.petstore.services.PetService;
import com.mastercard.app.petstore.utils.MockDataBuilders;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.Cat;
import org.openapitools.client.model.NewCat;
import org.openapitools.client.model.PetStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@ComponentScan(basePackages = {"com.mastercard.app.petstore.utils"})
@Component("PetFlowExample")
public class PetFlowExample {

    @Autowired
    private CatService catService;
    @Autowired
    private PetService petService;

    /**
     * Pet use case flow. Shows creating a cat, updating their status and then removing them
     *
     * @throws ApiException thrown whenever there is an issue sending a request
     */
    public void petUseCaseFlow () throws ApiException {
        //Add pet
        NewCat newCat = MockDataBuilders.buildNewCat();
        Cat cat = catService.addCat(newCat);
        cat = catService.getCat(cat.getId().toString());

        //Set pet status
        PetStatus status = new PetStatus().value("RESERVED");
        petService.updatePetStatus(cat.getId(), status,  "1");

        //Remove pet
        petService.removePet(cat.getId());
    }
}
