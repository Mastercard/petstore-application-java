package com.mastercard.app.petstore.examples;

import com.mastercard.app.petstore.services.AdoptionsService;
import com.mastercard.app.petstore.services.CatService;
import com.mastercard.app.petstore.utils.MockDataBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.Adoption;
import org.openapitools.client.model.Cat;
import org.openapitools.client.model.NewAdoption;
import org.openapitools.client.model.NewCat;

import java.util.UUID;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdoptionFlowExampleTest {

    @InjectMocks
    private AdoptionFlowExample adoptionFlowExample;

    @Mock
    private AdoptionsService adoptionsService;

    @Mock
    private CatService catService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAdoptionUseCase() throws ApiException {
        UUID catId = UUID.randomUUID();
        UUID adoptionId = UUID.randomUUID();

        NewCat newCat = mock(NewCat.class);
        Cat cat = mock(Cat.class);
        when(cat.getId()).thenReturn(catId);

        NewAdoption newAdoption = mock(NewAdoption.class);
        Adoption adoption = mock(Adoption.class);
        when(adoption.getId()).thenReturn(adoptionId);

        when(catService.addCat(any(NewCat.class))).thenReturn(cat);
        when(adoptionsService.adoptPet(any(NewAdoption.class))).thenReturn("/adoptions/" + adoptionId);
        when(adoptionsService.getAdoption(adoptionId.toString())).thenReturn(adoption);

        try (MockedStatic<MockDataBuilders> mockBuilders = mockStatic(MockDataBuilders.class)) {
            mockBuilders.when(MockDataBuilders::buildNewCat).thenReturn(newCat);
            mockBuilders.when(() -> MockDataBuilders.buildNewAdoptionObject(catId)).thenReturn(newAdoption);

            adoptionFlowExample.adoptionUseCase();

            verify(catService).addCat(newCat);
            verify(adoptionsService).adoptPet(newAdoption);
            verify(adoptionsService).getAdoption(adoptionId.toString());
            verify(adoptionsService).updateAdoption("0", adoption);
            verify(adoptionsService).deleteAdoption(adoptionId.toString());
        }
    }
}