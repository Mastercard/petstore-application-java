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
     * @param id the id
     * @return the cat
     * @throws ApiException the api exception
     */
    public Cat getCat(String id) throws ApiException {
        return catsApi.getCat(UUID.fromString(id));
    }

    /**
     * Adds a new cat.
     *
     * @param cat the cat
     * @return the cat
     * @throws ApiException the api exception
     */
    public Cat addCat(NewCat cat) throws ApiException {
        return catsApi.addCat(cat);
    }

    /**
     * Update cat data.
     *
     * @param cat  the cat
     * @param etag the etag. Cat be found in getCat
     * @throws ApiException the api exception
     */
    public void updateCat(Cat cat, String etag) throws ApiException {
        catsApi.updateCat(cat.getId(), etag, cat);
    }

}
