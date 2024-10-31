package com.mastercard.app.petstore.examples;

import com.mastercard.app.petstore.services.AdoptionsService;
import com.mastercard.app.petstore.utils.MockDataBuilders;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.Adoption;
import org.openapitools.client.model.AdoptionWrapper;
import org.openapitools.client.model.NewAdoption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * The type Adoption flow example.
 */

@ComponentScan(basePackages = {"com.mastercard.app.petstore.utils"})
@Component("AdoptionFLowExample")
public class AdoptionFlowExample {

    @Autowired
    private AdoptionsService adoptionsService;

    /**
     * Adoption use case. Shows a typical adding an adoption, adopting pet, updating status and then removing it
     *
     * @throws ApiException thrown whenever there is an issue sending a request
     */
    public void adoptionUseCase() throws ApiException {
        //Create adoption
        NewAdoption newAdoption = MockDataBuilders.buildNewAdoptionObject();
        adoptionsService.adoptPet(newAdoption);

        //Get Adoption
        Adoption adoption = adoptionsService.getAdoption(newAdoption.getPetId().toString());

        //Update Adoption
        adoptionsService.updateAdoption("1", adoption);

        //Remove Adoption
        adoptionsService.deleteAdoption(adoption.getId().toString());
    }
}
