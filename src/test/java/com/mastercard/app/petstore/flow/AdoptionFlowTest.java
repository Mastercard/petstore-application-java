package com.mastercard.app.petstore.flow;

import com.mastercard.app.petstore.TestMockBuilders;
import com.mastercard.app.petstore.services.AdoptionsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.Adoption;
import org.openapitools.client.model.AdoptionWrapper;
import org.openapitools.client.model.NewAdoption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Array;

import static org.mockito.Mockito.when;

/**
 * The type Adoption flow test.
 */
@SpringBootTest()
@RunWith(SpringRunner.class)
@ActiveProfiles({"oauth"})
@ComponentScan(basePackages = {"com.mastercard.app.petstore.utils"})
public class AdoptionFlowTest {

    /**
     * The Base path. Set in application.properties
     */
    @Value("${mastercard.basePath}")
    String basePath;

    @Mock
    private Environment environment;

    @Autowired
    private AdoptionsService adoptionsService;

    /**
     * Adoption use case. Shows a typical adding an adoption, adopting pet, updating status and then removing it
     *
     * @throws ApiException the api exception
     */
    @Test
    public void adoptionUseCase() throws ApiException {

        //Skipping test if applications.properties isn't set
        if(basePath == null){
            return;
        }
        //Create adoption
        NewAdoption newAdoption = TestMockBuilders.buildNewAdoptionObject();
        adoptionsService.adoptPet(newAdoption);

        //Get Adoption
        Adoption adoption = adoptionsService.getAdoption(newAdoption.getPetId().toString());

        //Update Adoption
        AdoptionWrapper adoptionWrapper = adoptionsService.updateAdoption("1", adoption);

        //Remove Adoption
        adoptionsService.deleteAdoption(adoption.getId().toString());
    }
}
