package com.mastercard.app.petstore.services;

import org.openapitools.client.ApiException;
import org.openapitools.client.api.AdoptionsApi;
import org.openapitools.client.model.Adoption;
import org.openapitools.client.model.AdoptionSearch;
import org.openapitools.client.model.AdoptionWrapper;
import org.openapitools.client.model.NewAdoption;
import org.openapitools.client.model.PaymentDetails;
import org.openapitools.client.model.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class AdoptionsService {

    private final AdoptionsApi adoptionsApiFle;
    private final AdoptionsApi adoptionsApiFullBody;

    /**
     * Instantiates a new Adoptions service.
     *
     * @param adoptionsApiFle field level encryption api. Only certain fields will be encrypted
     * @param adoptionsApiFullBody full body encryption api. The entire payload will be encrypted
     */
    @Autowired
    public AdoptionsService(AdoptionsApi adoptionsApiFle, AdoptionsApi adoptionsApiFullBody){
        this.adoptionsApiFle = adoptionsApiFle;
        this.adoptionsApiFullBody = adoptionsApiFullBody;
    }

    /**
     * Adopt pet. Preforms an adoption on an existing pet
     *
     * @param adoption the adoption
     * @throws ApiException the api exception
     */
    public void adoptPet(NewAdoption adoption) throws ApiException {
        adoptionsApiFle.adoptPet(adoption);
    }

    /**
     * Gets an existing adoption.
     *
     * @param id the id of the adoption
     * @return the adoption
     * @throws ApiException the api exception
     */
    public Adoption getAdoption(String id) throws ApiException {
        return adoptionsApiFle.getAdoption(UUID.fromString(id));
    }

    /**
     * Searches all existing adoptions.
     *
     * @param fromDate    the from date
     * @param toDate      the to date
     * @param petCategory the pet category
     * @param petId       the pet id
     * @return the adoption search
     * @throws ApiException the api exception
     */
    public AdoptionSearch searchAdoption(Date fromDate, Date toDate, String petCategory, String petId) throws ApiException {
        return adoptionsApiFle.searchAdoptedPets(fromDate, toDate, petCategory, UUID.fromString(petId));
    }

    /**
     * Updates adoption data.
     *
     * @param etag the etag. Cat be found in getAdoption
     * @param adoption the adoption
     * @return the adoption wrapper
     * @throws ApiException the api exception
     */
    public AdoptionWrapper updateAdoption(String etag, Adoption adoption) throws ApiException {
        AdoptionWrapper adoptionWrapper = new AdoptionWrapper();
        adoptionWrapper.setAdoption(adoption);
        return adoptionsApiFle.updateAdoption(adoption.getId(), etag, adoptionWrapper);
    }

    /**
     * Delete adoption.
     *
     * @param id the id
     * @throws ApiException the api exception
     */
    public void deleteAdoption(String id) throws ApiException {
        adoptionsApiFle.deleteAdoption(UUID.fromString(id));
    }

    /**
     * Make a payment to adopt a pet. Sets adoption status to ADOPTED on payment confirmation
     *
     * @param id      the id
     * @param payment the payment
     * @return the payment details
     * @throws ApiException the api exception
     */
    public PaymentDetails adoptionPayment(String id, Payment payment) throws ApiException {
        return adoptionsApiFullBody.adoptionPayment(UUID.fromString(id), payment);
    }

}