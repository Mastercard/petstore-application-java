package com.mastercard.app.petstore;

import com.mastercard.app.petstore.services.AdoptionsService;
import com.mastercard.app.petstore.utils.MockDataBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.openapitools.client.ApiException;
import org.openapitools.client.ApiResponse;
import org.openapitools.client.api.AdoptionsApi;
import org.openapitools.client.model.Adoption;
import org.openapitools.client.model.AdoptionSearch;
import org.openapitools.client.model.NewAdoption;
import org.openapitools.client.model.Payment;
import org.openapitools.client.model.PaymentDetails;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AdoptionsServiceTest {

    private AdoptionsApi adoptionsApiFle;

    private AdoptionsApi adoptionsApiFullBody;

    @InjectMocks
    AdoptionsService adoptionsService;

    @BeforeEach
    public void setUp() {
        adoptionsApiFle = mock(AdoptionsApi.class);
        adoptionsApiFullBody = mock(AdoptionsApi.class);
        adoptionsService = new AdoptionsService(adoptionsApiFle, adoptionsApiFullBody);
    }

    @Test
    public void getAdoption_shouldReturnAnAdoption() throws ApiException {
        Adoption adoption = MockDataBuilders.buildAdoptionObject();
        when(adoptionsApiFle.getAdoption(any())).thenReturn(adoption);

        Adoption returnedAdoption = adoptionsService.getAdoption(adoption.getId().toString());

        assertEquals(adoption.getId(), returnedAdoption.getId());
    }

    @Test
    public void searchAdoption_shouldReturnAnAdoptionSearch() throws ApiException {
        AdoptionSearch adoptionSearch = MockDataBuilders.buildAdoptionSearch();

        when(adoptionsApiFle.searchAdoptedPets(adoptionSearch.getFromDate(), adoptionSearch.getToDate(), adoptionSearch.getPetCategory(), adoptionSearch.getPetIdentifier())).thenReturn(adoptionSearch);

        AdoptionSearch returnedAdoptionSearch = adoptionsService.searchAdoption(adoptionSearch.getFromDate(), adoptionSearch.getToDate(), adoptionSearch.getPetCategory(), adoptionSearch.getPetIdentifier().toString());

        assertEquals(adoptionSearch.getSearchResults().size(), returnedAdoptionSearch.getSearchResults().size());
    }

    @Test
    public void adoptionPayment_shouldReturnAnAdoptionPayment() throws ApiException {
        Payment payment = MockDataBuilders.buildPayment();
        PaymentDetails paymentDetails = MockDataBuilders.buildPaymentDetailsFromPayment(payment);
        UUID id = UUID.randomUUID();

        when(adoptionsApiFullBody.adoptionPayment(id, payment)).thenReturn(paymentDetails);

        PaymentDetails returnedPaymentDetails = adoptionsService.adoptionPayment(id.toString(), payment);

        assertEquals(payment.getAmount(), returnedPaymentDetails.getAmount());
    }


}
