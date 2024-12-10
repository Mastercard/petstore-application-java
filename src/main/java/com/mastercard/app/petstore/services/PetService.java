package com.mastercard.app.petstore.services;

import org.openapitools.client.ApiException;
import org.openapitools.client.api.PetsApi;
import org.openapitools.client.model.PetList;
import org.openapitools.client.model.PetStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PetService {
    private final PetsApi petsApi;

    @Autowired
    public PetService(PetsApi petsApi) {
        this.petsApi = petsApi;
    }

    /**
     * Update pet status.
     *
     * @param id        the pets id
     * @param status    the adoption status of the pet. Can be
     *                  AVAILABLE,
     *                  RESERVED,
     *                  ADOPTED,
     *                  UNDER_EXAMINATION;
     * @param etag      the etag
     * @throws ApiException thrown whenever there is an issue sending a request
     */
    public void updatePetStatus(UUID id, PetStatus status, String etag) throws ApiException {
        petsApi.updatePetStatus(id, etag, status);
    }

    /**
     * Search for multiple pets based on status.
     *
     * @param status        the adoption status of the pet. Can be
     *                      AVAILABLE,
     *                      RESERVED,
     *                      ADOPTED,
     *                      UNDER_EXAMINATION;
     * @return the list of pets that match the status
     * @throws ApiException thrown whenever there is an issue sending a request
     */
    public PetList searchForPets(String status) throws ApiException {
        return petsApi.searchPets(status, 10, 0, "+");
    }

    /**
     * Remove pet.
     *
     * @param id             the id of the pet to be deleted. In UUID format
     * @throws ApiException  thrown whenever there is an issue sending a request
     */
    public void removePet(UUID id) throws ApiException {
        petsApi.deletePet(id);
    }
}
