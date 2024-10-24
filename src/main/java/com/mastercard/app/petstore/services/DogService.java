package com.mastercard.app.petstore.services;

import org.openapitools.client.ApiException;
import org.openapitools.client.api.DogsApi;
import org.openapitools.client.model.Dog;
import org.openapitools.client.model.NewDog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DogService {
    private final DogsApi dogsApi;

    @Autowired
    public DogService(DogsApi dogsApi) {
        this.dogsApi = dogsApi;
    }

    /**
     * Gets information on a dog.
     *
     * @param id the id
     * @return the dog
     * @throws ApiException the api exception
     */
    public Dog getDog(String id) throws ApiException {
        return dogsApi.getDog(UUID.fromString(id));
    }

    /**
     * Add a new dog.
     *
     * @param dog the dog
     * @return the dog
     * @throws ApiException the api exception
     */
    public Dog addDog(NewDog dog) throws ApiException {
        return dogsApi.addDog(dog);
    }

    /**
     * Update dog data.
     *
     * @param dog  the dog
     * @param etag the etag. Can be found in getDog
     * @throws ApiException the api exception
     */
    public void updateDog(Dog dog, String etag) throws ApiException {
        dogsApi.updateDog(dog.getId(), etag, dog);
    }

}
