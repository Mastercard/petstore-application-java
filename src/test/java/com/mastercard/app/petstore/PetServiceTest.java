package com.mastercard.app.petstore;

import com.mastercard.app.petstore.services.PetService;
import com.mastercard.app.petstore.utils.MockDataBuilders;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.PetsApi;
import org.openapitools.client.model.Pet;
import org.openapitools.client.model.PetList;
import org.openapitools.client.model.PetStatus;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PetServiceTest {

    private PetsApi petsApi;

    @InjectMocks
    PetService petService;

    @BeforeEach
    public void setUp() {
        petsApi = mock(PetsApi.class);
        petService = new PetService(petsApi);
    }

    @Test
    public void updatePet_shouldUpdateAPet() throws ApiException {
        Pet pet = MockDataBuilders.buildPet();
        PetStatus petStatus = MockDataBuilders.buildPetStatus();
        String etag = "33a64df551425f";

        doNothing().when(petsApi).updatePetStatus(pet.getId(), etag, petStatus);

        petService.updatePetStatus(pet.getId(), petStatus, etag);

        verify(petsApi, times(1)).updatePetStatus(pet.getId(), etag, petStatus);
    }

    @Test
    public void searchForPets_shouldReturnAListofPets() throws ApiException {
        String status = "RESERVED";
        List<Pet> petCollection = Collections.singletonList(MockDataBuilders.buildPet());
        PetList pets = new PetList();
        pets.setItems(petCollection);
        pets.setLimit(10);
        pets.setOffset(0);
        pets.setTotal(1);
        pets.setCount(1);

        when(petsApi.searchPets(status, 10, 0, "+")).thenReturn(pets);

        PetList returnedPets = petService.searchForPets(status);

        assertEquals(petCollection.size(), returnedPets.getItems().size());
    }

    @Test
    public void removePet_shouldRemoveAPet() throws ApiException {
        Pet pet = MockDataBuilders.buildPet();

        doNothing().when(petsApi).deletePet(pet.getId());

        petService.removePet(pet.getId());

        verify(petsApi, times(1)).deletePet(pet.getId());
    }

}
