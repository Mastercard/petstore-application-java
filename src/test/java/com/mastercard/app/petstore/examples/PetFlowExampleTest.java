package com.mastercard.app.petstore.examples;

import com.mastercard.app.petstore.services.CatService;
import com.mastercard.app.petstore.services.PetService;
import com.mastercard.app.petstore.utils.MockDataBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.Cat;
import org.openapitools.client.model.NewCat;
import org.openapitools.client.model.PetStatus;

import java.util.Date;
import java.util.UUID;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PetFlowExampleTest {

    @InjectMocks
    private PetFlowExample petFlowExample;

    @Mock
    private CatService catService;

    @Mock
    private PetService petService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPetUseCaseFlow() throws ApiException {
        NewCat newCat = new NewCat();
        UUID catId = UUID.randomUUID();
        Cat cat = new Cat(catId, new Date(), new Date()).name("Whiskers");
        PetStatus status = new PetStatus().value("RESERVED");

        when(catService.addCat(any(NewCat.class))).thenReturn(cat);
        when(catService.getCat(catId.toString())).thenReturn(cat);

        try (MockedStatic<MockDataBuilders> mockBuilders = mockStatic(MockDataBuilders.class)) {
            mockBuilders.when(MockDataBuilders::buildNewCat).thenReturn(newCat);

            petFlowExample.petUseCaseFlow();

            verify(catService).addCat(newCat);
            verify(catService).getCat(catId.toString());
            verify(catService).updateCat(argThat(updatedCat -> "Catso".equals(updatedCat.getName())), eq("0"));
            verify(petService).updatePetStatus(eq(catId), eq(status), eq("1"));
            verify(petService).removePet(catId);
        }
    }

    @Test
    void testPetUseCaseFlowThrowsApiException() throws ApiException {
        NewCat newCat = new NewCat();
        when(catService.addCat(any(NewCat.class))).thenThrow(new ApiException("API error"));

        try (MockedStatic<MockDataBuilders> mockBuilders = mockStatic(MockDataBuilders.class)) {
            mockBuilders.when(MockDataBuilders::buildNewCat).thenReturn(newCat);

            org.junit.jupiter.api.Assertions.assertThrows(ApiException.class, () -> petFlowExample.petUseCaseFlow());
        }
    }
}
