package com.mastercard.app.petstore.services;

import com.mastercard.app.petstore.utils.MockDataBuilders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DogsApi;
import org.openapitools.client.model.Dog;
import org.openapitools.client.model.NewDog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DogServiceTest {

    private DogsApi dogsApi;

    @InjectMocks
    DogService dogService;

    @BeforeEach
    void setUp() {
        dogsApi = mock(DogsApi.class);
        dogService = new DogService(dogsApi);
    }

    @Test
    void addDog_shouldReturnADog() throws ApiException {
        NewDog newDog = MockDataBuilders.buildNewDog();
        Dog expectedDog = MockDataBuilders.buildDog();

        when(dogsApi.addDog(any())).thenReturn(expectedDog);

        Dog returnedDog = dogService.addDog(newDog);

        assertEquals(expectedDog.getId(), returnedDog.getId());
    }

    @Test
    void getDog_shouldReturnADog() throws ApiException {
        Dog dog = MockDataBuilders.buildDog();
        when(dogsApi.getDog(any())).thenReturn(dog);

        Assertions.assertNotNull(dog.getId());
        Dog returnedDog = dogService.getDog(dog.getId().toString());

        assertEquals(returnedDog.getId(), dog.getId());
    }

    @Test
    void updateDog_shouldUpdateADog() throws ApiException {
        Dog dog = MockDataBuilders.buildDog();
        String etag = "33a64df551425f";

        doNothing().when(dogsApi).updateDog(dog.getId(), etag, dog);

        dogService.updateDog(dog, etag);

        verify(dogsApi, times(1)).updateDog(dog.getId(), etag, dog);
    }


}
