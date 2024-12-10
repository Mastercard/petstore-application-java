package com.mastercard.app.petstore.services;

import org.openapitools.client.ApiException;
import org.openapitools.client.ApiResponse;
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
     * @param adoptionsApiFle       field level encryption api. Only certain fields will be encrypted
     * @param adoptionsApiFullBody  full body encryption api. The entire payload will be encrypted
     */
    @Autowired
    public AdoptionsService(AdoptionsApi adoptionsApiFle, AdoptionsApi adoptionsApiFullBody){
        this.adoptionsApiFle = adoptionsApiFle;
        this.adoptionsApiFullBody = adoptionsApiFullBody;
    }

    /**
     * Adopt pet. Preforms an adoption on an existing pet
     *
     * @param adoption          a NewAdoption object. Contains information about the pet that will be adopted
     * @throws ApiException     thrown whenever there is an issue sending a request
     * return a string with a link to the adoption
     */
    public String adoptPet(NewAdoption adoption) throws ApiException {
        ApiResponse<Void> response = adoptionsApiFle.adoptPetWithHttpInfo(adoption);
        return response.getHeaders().get("location").get(0);
    }

    /**
     * Gets an existing adoption.
     *
     * @param id            the id of the adoption
     * @return an adoption object containing information of the adoption
     * @throws ApiException thrown whenever there is an issue sending a request
     */
    public Adoption getAdoption(String id) throws ApiException {
        return adoptionsApiFle.getAdoption(UUID.fromString(id));
    }

    /**
     * Searches all existing adoptions.
     *
     * @param fromDate    the adoption from date
     * @param toDate      the adoption to date
     * @param petCategory the pet category. Can be CAT or DOG
     * @param petId       the pet id. In UUID format
     * @return the adoption search. A list of all adoption records
     * @throws ApiException thrown whenever there is an issue sending a request
     */
    public AdoptionSearch searchAdoption(Date fromDate, Date toDate, String petCategory, String petId) throws ApiException {
        return adoptionsApiFle.searchAdoptedPets(fromDate, toDate, petCategory, UUID.fromString(petId));
    }

    /**
     * Updates adoption data.
     *
     * @param etag          the etag. Can be found in getAdoption
     * @param adoption      the adoption. Contain information about an adoption to be updated
     * @return adoptionWrapper. A wrapper object containing an adoption record
     * @throws ApiException thrown whenever there is an issue sending a request
     */
    public AdoptionWrapper updateAdoption(String etag, Adoption adoption) throws ApiException {
        AdoptionWrapper adoptionWrapper = new AdoptionWrapper();
        adoptionWrapper.setAdoption(adoption);
        return adoptionsApiFle.updateAdoption(adoption.getId(), etag, adoptionWrapper);
    }

    /**
     * Delete adoption.
     *
     * @param id            the adoption id. In UUID format
     * @throws ApiException thrown whenever there is an issue sending a request
     */
    public void deleteAdoption(String id) throws ApiException {
        adoptionsApiFle.deleteAdoption(UUID.fromString(id));
    }

    /**
     * Make a payment to adopt a pet. Sets adoption status to ADOPTED on payment confirmation
     *
     * @param id                the adoption id. In UUID format
     * @param payment           the payment. The payment information for purchasing a pet
     * @return paymentDetails the payment details. Receipt containing information on payment and if it was a success
     * @throws ApiException     thrown whenever there is an issue sending a request
     */
    public PaymentDetails adoptionPayment(String id, Payment payment) throws ApiException {
        return adoptionsApiFullBody.adoptionPayment(UUID.fromString(id), payment);
    }

}