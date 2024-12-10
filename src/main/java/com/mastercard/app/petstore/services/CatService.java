package com.mastercard.app.petstore.services;

import org.openapitools.client.ApiException;
import org.openapitools.client.api.CatsApi;
import org.openapitools.client.model.Cat;
import org.openapitools.client.model.NewCat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CatService {

    private final CatsApi catsApi;

    @Autowired
    public CatService(CatsApi catsApi) {
        this.catsApi = catsApi;
    }

    /**
     * Gets information on a cat.
     *
     * @param id            the id of a cat. In UUID format
     * @return the cat. containing information about a cat, extends pet.
     * @throws ApiException thrown whenever there is an issue sending a request
     */
    public Cat getCat(String id) throws ApiException {
        return catsApi.getCat(UUID.fromString(id));
    }

    /**
     * Adds a new cat.
     *
     * @param cat a NewCat object containing information about a cat, extends pet
     * @return the cat. containing information about a cat, extends pet. Has extra fields set by the server
     * @throws ApiException thrown whenever there is an issue sending a request
     */
    public Cat addCat(NewCat cat) throws ApiException {
        return catsApi.addCat(cat);
    }

    /**
     * Update cat data.
     *
     * @param cat contains information about a cat, extends pet. Will update the cat
     * @param etag the etag. Cat be found in getCat
     * @throws ApiException thrown whenever there is an issue sending a request
     */
    public void updateCat(Cat cat, String etag) throws ApiException {
        catsApi.updateCat(cat.getId(), etag, cat);
    }

}
