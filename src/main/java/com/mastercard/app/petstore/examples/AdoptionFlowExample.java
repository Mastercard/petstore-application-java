package com.mastercard.app.petstore.examples;

import com.mastercard.app.petstore.services.AdoptionsService;
import com.mastercard.app.petstore.services.CatService;
import com.mastercard.app.petstore.utils.MockDataBuilders;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.Adoption;
import org.openapitools.client.model.Cat;
import org.openapitools.client.model.NewAdoption;
import org.openapitools.client.model.NewCat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * The type Adoption flow example.
 */

@Component("AdoptionFLowExample")
public class AdoptionFlowExample {

    private AdoptionsService adoptionsService;
    private CatService catService;

    /**
     * Constructs an instance of {@code AdoptionsService} with required dependencies.
     * <p>
     * This constructor is annotated with {@link org.springframework.beans.factory.annotation.Autowired}
     * so that Spring can automatically inject the required beans at runtime.
     * </p>
     *
     * @param adoptionsService the service responsible for handling adoption operations
     * @param catService       the service responsible for managing cat-related operations
     */
    @Autowired
    public AdoptionsService(AdoptionsService adoptionsService, CatService catService){
        this.adoptionsService = adoptionsService;
        this.catService = catService;
    }
    /**
     * Adoption use case. Shows a typical adding an adoption, adopting pet, updating status and then removing it
     *
     * @throws ApiException thrown whenever there is an issue sending a request
     */
    public void adoptionUseCase() throws ApiException {
        //Add cat
        NewCat newCat = MockDataBuilders.buildNewCat();
        Cat cat = catService.addCat(newCat);

        //Create adoption
        NewAdoption newAdoption = MockDataBuilders.buildNewAdoptionObject(cat.getId());
        String location = adoptionsService.adoptPet(newAdoption);
        String adoptionId = location.substring(location.lastIndexOf('/') + 1).trim();

        //Get Adoption
        Adoption adoption = adoptionsService.getAdoption(adoptionId);

        //Update Adoption
        adoptionsService.updateAdoption("0", adoption);

        //Remove Adoption
        adoptionsService.deleteAdoption(adoption.getId().toString());
    }
}
