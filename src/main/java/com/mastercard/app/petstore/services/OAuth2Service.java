package com.mastercard.app.petstore.services;

import org.openapitools.client.ApiException;
import org.openapitools.client.api.AuthenticationApi;
import org.openapitools.client.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The type Employee service.
 */
@Service
public class OAuth2Service {
    private final AuthenticationApi authenticationApi;

    @Autowired
    public OAuth2Service(AuthenticationApi authenticationApi) {
        this.authenticationApi = authenticationApi;
    }

    // Create a new OAuth2 token
    public Token createToken() throws ApiException {
        return authenticationApi.createToken();
    }

    // Use OAuth2 token to get a test resource
    public TestResource getTestResource(String auth) throws ApiException {
        return authenticationApi.getTestResource(auth);
    }
}
