package com.mastercard.app.petstore.examples;

import com.mastercard.app.petstore.services.AdoptionsService;
import com.mastercard.app.petstore.utils.MockDataBuilders;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.Adoption;
import org.openapitools.client.model.AdoptionWrapper;
import org.openapitools.client.model.NewAdoption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * The type Adoption flow example.
 */

@ComponentScan(basePackages = {"com.mastercard.app.petstore.utils"})
@Component("AdoptionFLowExample")
public class AdoptionFlowExample {

    /**
     * The Base path. Set in application.properties
     */
    @Value("${mastercard.basePath}")
    String basePath;

    private Environment environment;

    @Autowired
    private AdoptionsService adoptionsService;

    /**
     * Adoption use case. Shows a typical adding an adoption, adopting pet, updating status and then removing it
     *
     * @throws ApiException the api exception
     */
    public void adoptionUseCase() throws ApiException {

        //Skipping test if applications.properties isn't set
        if(basePath == null){
            return;
        }
        //Create adoption
        NewAdoption newAdoption = MockDataBuilders.buildNewAdoptionObject();
        adoptionsService.adoptPet(newAdoption);

        //Get Adoption
        Adoption adoption = adoptionsService.getAdoption(newAdoption.getPetId().toString());

        //Update Adoption
        AdoptionWrapper adoptionWrapper = adoptionsService.updateAdoption("1", adoption);

        //Remove Adoption
        adoptionsService.deleteAdoption(adoption.getId().toString());
    }
}
