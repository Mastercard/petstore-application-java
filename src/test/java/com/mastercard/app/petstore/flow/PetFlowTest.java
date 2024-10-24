package com.mastercard.app.petstore.flow;

import com.mastercard.app.petstore.TestMockBuilders;
import com.mastercard.app.petstore.services.CatService;
import com.mastercard.app.petstore.services.PetService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.CatsApi;
import org.openapitools.client.api.PetsApi;
import org.openapitools.client.model.Cat;
import org.openapitools.client.model.NewCat;
import org.openapitools.client.model.PetStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest()
@RunWith(SpringRunner.class)
@ActiveProfiles({"oauth"})
@ComponentScan(basePackages = {"com.mastercard.app.petstore.utils"})
public class PetFlowTest {

    /**
     * The Base path.
     */
    @Value("${mastercard.basePath}")
    String basePath;

    @Autowired
    private CatService catService;
    @Autowired
    private PetService petService;

    /**
     * Pet use case flow. Shows creating a cat, updating their status and then removing them
     *
     * @throws ApiException the api exception
     */
    @Test
    public void petUseCaseFlow () throws ApiException {
        //Skipping test if applications.properties isn't set
        if(basePath == null){
            return;
        }

        //Add pet
        NewCat newCat = TestMockBuilders.buildNewCat();
        Cat cat = catService.addCat(newCat);
        cat = catService.getCat(cat.getId().toString());

        //Set pet status
        PetStatus status = new PetStatus().value("RESERVED");
        petService.updatePetStatus(cat.getId(), status,  "1");

        //Remove pet
        petService.removePet(cat.getId());
    }
}
