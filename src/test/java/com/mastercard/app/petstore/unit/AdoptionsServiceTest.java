package com.mastercard.app.petstore.unit;

import com.mastercard.app.petstore.TestMockBuilders;
import com.mastercard.app.petstore.services.AdoptionsService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.openapitools.client.ApiException;
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

    @Before
    public void setUp() {
        adoptionsApiFle = mock(AdoptionsApi.class);
        adoptionsApiFullBody = mock(AdoptionsApi.class);
        adoptionsService = new AdoptionsService(adoptionsApiFle, adoptionsApiFullBody);
    }

    @Test
    public void adoptPet_shouldRun() throws ApiException {
        NewAdoption newAdoption = TestMockBuilders.buildNewAdoptionObject();
        doNothing().when(adoptionsApiFle).adoptPet(newAdoption);

        adoptionsService.adoptPet(newAdoption);

        verify(adoptionsApiFle, times(1)).adoptPet(newAdoption);
    }

    @Test
    public void getAdoption_shouldReturnAnAdoption() throws ApiException {
        Adoption adoption = TestMockBuilders.buildAdoptionObject();
        when(adoptionsApiFle.getAdoption(any())).thenReturn(adoption);

        Adoption returnedAdoption = adoptionsService.getAdoption(adoption.getId().toString());

        assertEquals(adoption.getId(), returnedAdoption.getId());
    }

    @Test
    public void searchAdoption_shouldReturnAnAdoptionSearch() throws ApiException {
        AdoptionSearch adoptionSearch = TestMockBuilders.buildAdoptionSearch();

        when(adoptionsApiFle.searchAdoptedPets(adoptionSearch.getFromDate(), adoptionSearch.getToDate(), adoptionSearch.getPetCategory(), adoptionSearch.getPetIdentifier())).thenReturn(adoptionSearch);

        AdoptionSearch returnedAdoptionSearch = adoptionsService.searchAdoption(adoptionSearch.getFromDate(), adoptionSearch.getToDate(), adoptionSearch.getPetCategory(), adoptionSearch.getPetIdentifier().toString());

        assertEquals(adoptionSearch.getSearchResults().size(), returnedAdoptionSearch.getSearchResults().size());
    }

    @Test
    public void adoptionPayment_shouldReturnAnAdoptionPayment() throws ApiException {
        Payment payment = TestMockBuilders.buildPayment();
        PaymentDetails paymentDetails = TestMockBuilders.buildPaymentDetailsFromPayment(payment);
        UUID id = UUID.randomUUID();

        when(adoptionsApiFullBody.adoptionPayment(id, payment)).thenReturn(paymentDetails);

        PaymentDetails returnedPaymentDetails = adoptionsService.adoptionPayment(id.toString(), payment);

        assertEquals(payment.getAmount(), returnedPaymentDetails.getAmount());
    }


}
