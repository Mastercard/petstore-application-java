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
     * @param id the id of a dog. In UUID format
     * @return the dog. containing information about a dog, extends pet.
     * @throws ApiException thrown whenever there is an issue sending a request
     */
    public Dog getDog(String id) throws ApiException {
        return dogsApi.getDog(UUID.fromString(id));
    }

    /**
     * Adds a new dog.
     *
     * @param dog a NewCat object containing information about a dog, extends pet
     * @return the dog. containing information about a dog, extends pet. Has extra fields set by the server
     * @throws ApiException thrown whenever there is an issue sending a request
     */
    public Dog addDog(NewDog dog) throws ApiException {
        return dogsApi.addDog(dog);
    }

    /**
     * Update dog data.
     *
     * @param dog contains information about a dog, extends pet. Will update the dog
     * @param etag the etag. Cat be found in getCat
     * @throws ApiException thrown whenever there is an issue sending a request
     */
    public void updateDog(Dog dog, String etag) throws ApiException {
        dogsApi.updateDog(dog.getId(), etag, dog);
    }

}
