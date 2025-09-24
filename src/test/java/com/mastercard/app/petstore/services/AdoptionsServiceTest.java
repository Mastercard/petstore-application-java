package com.mastercard.app.petstore.services;

import com.mastercard.app.petstore.utils.MockDataBuilders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.openapitools.client.ApiException;
import org.openapitools.client.ApiResponse;
import org.openapitools.client.api.AdoptionsApi;
import org.openapitools.client.model.Adoption;
import org.openapitools.client.model.AdoptionSearch;
import org.openapitools.client.model.AdoptionWrapper;
import org.openapitools.client.model.Payment;
import org.openapitools.client.model.PaymentDetails;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdoptionsServiceTest {

    private AdoptionsApi adoptionsApiFle;

    private AdoptionsApi adoptionsApiFullBody;

    @InjectMocks
    AdoptionsService adoptionsService;

    @BeforeEach
    void setUp() {
        adoptionsApiFle = mock(AdoptionsApi.class);
        adoptionsApiFullBody = mock(AdoptionsApi.class);
        adoptionsService = new AdoptionsService(adoptionsApiFle, adoptionsApiFullBody);
    }

    @Test
    void adoptPet_shouldReturnALocation() throws ApiException {
        Map<String, List<String>> headers =  Collections.singletonMap("location", Collections.singletonList("/adoptions/b1546cc7-a979-4c13-8817-40a8a0770868"));
        ApiResponse<Void> responseWithHeader = new ApiResponse<>(200, headers);
        when(adoptionsApiFle.adoptPetWithHttpInfo(any())).thenReturn(responseWithHeader);

        String location = adoptionsService.adoptPet(MockDataBuilders.buildNewAdoptionObject());

        assertEquals("/adoptions/b1546cc7-a979-4c13-8817-40a8a0770868", location);
    }

    @Test
    void getAdoption_shouldReturnAnAdoption() throws ApiException {
        Adoption adoption = MockDataBuilders.buildAdoptionObject();
        when(adoptionsApiFle.getAdoption(any())).thenReturn(adoption);

        Assertions.assertNotNull(adoption.getId());
        Adoption returnedAdoption = adoptionsService.getAdoption(adoption.getId().toString());

        assertEquals(adoption.getId(), returnedAdoption.getId());
    }

    @Test
    void searchAdoption_shouldReturnAnAdoptionSearch() throws ApiException {
        AdoptionSearch adoptionSearch = MockDataBuilders.buildAdoptionSearch();

        when(adoptionsApiFle.searchAdoptedPets(adoptionSearch.getFromDate(), adoptionSearch.getToDate(), adoptionSearch.getPetCategory(), adoptionSearch.getPetIdentifier())).thenReturn(adoptionSearch);

        Assertions.assertNotNull(adoptionSearch.getPetIdentifier());
        AdoptionSearch returnedAdoptionSearch = adoptionsService.searchAdoption(adoptionSearch.getFromDate(), adoptionSearch.getToDate(), adoptionSearch.getPetCategory(), adoptionSearch.getPetIdentifier().toString());

        assertEquals(adoptionSearch.getSearchResults().size(), returnedAdoptionSearch.getSearchResults().size());
    }

    @Test
    void updateAdoption_shouldUpdateAnAdoption() throws ApiException {
        Adoption adoption = MockDataBuilders.buildAdoptionObject();
        String etag = "33a64df551425f";

        when(adoptionsApiFle.updateAdoption(
                eq(adoption.getId()),
                eq(etag),
                argThat(wrapper -> adoption.equals(wrapper.getAdoption())))
        ).thenReturn(new AdoptionWrapper());

        adoptionsService.updateAdoption(etag, adoption);

        verify(adoptionsApiFle, times(1)).updateAdoption(
                eq(adoption.getId()),
                eq(etag),
                argThat(wrapper -> adoption.equals(wrapper.getAdoption()))
        );
    }

    @Test
    void deleteAdoption_shouldDeleteAnAdoption() throws ApiException {
        UUID id = UUID.randomUUID();
        doNothing().when(adoptionsApiFle).deleteAdoption(id);

        adoptionsService.deleteAdoption(id.toString());

        verify(adoptionsApiFle, times(1)).deleteAdoption(id);
    }

    @Test
    void adoptionPayment_shouldReturnAnAdoptionPayment() throws ApiException {
        Payment payment = MockDataBuilders.buildPayment();
        PaymentDetails paymentDetails = MockDataBuilders.buildPaymentDetailsFromPayment(payment);
        UUID id = UUID.randomUUID();

        when(adoptionsApiFullBody.adoptionPayment(id, payment)).thenReturn(paymentDetails);

        PaymentDetails returnedPaymentDetails = adoptionsService.adoptionPayment(id.toString(), payment);

        assertEquals(payment.getAmount(), returnedPaymentDetails.getAmount());
    }


}
