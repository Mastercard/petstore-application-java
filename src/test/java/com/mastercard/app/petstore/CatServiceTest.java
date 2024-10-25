package com.mastercard.app.petstore;

import com.mastercard.app.petstore.services.CatService;
import com.mastercard.app.petstore.utils.MockDataBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.CatsApi;
import org.openapitools.client.model.Cat;
import org.openapitools.client.model.NewCat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CatServiceTest {

    private CatsApi catsApi;

    @InjectMocks
    CatService catService;

    @BeforeEach
    public void setUp() {
        catsApi = mock(CatsApi.class);
        catService = new CatService(catsApi);
    }

    @Test
    public void addCat_shouldReturnACat() throws ApiException {
        NewCat newCat = MockDataBuilders.buildNewCat();
        Cat expectedCat = MockDataBuilders.buildCat();

        when(catsApi.addCat(any())).thenReturn(expectedCat);

        Cat returnedCat = catService.addCat(newCat);

        assertEquals(expectedCat.getId(), returnedCat.getId());
    }

    @Test
    public void getCat_shouldReturnACat() throws ApiException {
        Cat cat = MockDataBuilders.buildCat();
        when(catsApi.getCat(any())).thenReturn(cat);

        Cat returnedCat = catService.getCat(cat.getId().toString());

        assertEquals(returnedCat.getId(), cat.getId());
    }

    @Test
    public void updateCat_shouldUpdateACat() throws ApiException {
        Cat cat = MockDataBuilders.buildCat();
        String etag = "33a64df551425f";

        doNothing().when(catsApi).updateCat(cat.getId(), etag, cat);

        catService.updateCat(cat, etag);

        verify(catsApi, times(1)).updateCat(cat.getId(), etag, cat);
    }


}
