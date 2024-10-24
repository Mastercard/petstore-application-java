package com.mastercard.app.petstore.unit;

import com.mastercard.app.petstore.TestMockBuilders;
import com.mastercard.app.petstore.services.DogService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DogsApi;
import org.openapitools.client.model.Dog;
import org.openapitools.client.model.NewDog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DogServiceTest {

    private DogsApi dogsApi;

    @InjectMocks
    DogService dogService;

    @Before
    public void setUp() {
        dogsApi = mock(DogsApi.class);
        dogService = new DogService(dogsApi);
    }

    @Test
    public void addDog_shouldReturnADog() throws ApiException {
        NewDog newDog = TestMockBuilders.buildNewDog();
        Dog expectedDog = TestMockBuilders.buildDog();

        when(dogsApi.addDog(any())).thenReturn(expectedDog);

        Dog returnedDog = dogService.addDog(newDog);

        assertEquals(expectedDog.getId(), returnedDog.getId());
    }

    @Test
    public void getDog_shouldReturnADog() throws ApiException {
        Dog dog = TestMockBuilders.buildDog();
        when(dogsApi.getDog(any())).thenReturn(dog);

        Dog returnedDog = dogService.getDog(dog.getId().toString());

        assertEquals(returnedDog.getId(), dog.getId());
    }

    @Test
    public void updateDog_shouldUpdateADog() throws ApiException {
        Dog dog = TestMockBuilders.buildDog();
        String etag = "33a64df551425f";

        doNothing().when(dogsApi).updateDog(dog.getId(), etag, dog);

        dogService.updateDog(dog, etag);

        verify(dogsApi, times(1)).updateDog(dog.getId(), etag, dog);
    }


}
