package com.mastercard.app.petstore.examples;

import com.mastercard.app.petstore.services.OAuth2Service;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;

@ComponentScan(basePackages = {"com.mastercard.app.petstore.utils"})
@Component("OAuth2FlowExample")
public class OAuth2FlowExample {

    private static final Logger log = LoggerFactory.getLogger(OAuth2FlowExample.class);

    @Autowired
    private OAuth2Service oAuth2Service;

    public void oauth2UseCase() throws ApiException, JOSEException, NoSuchAlgorithmException {
        // Create Transient Keys
        RSAKey rsaJWK = new RSAKeyGenerator(2048)
                .keyID("1")
                .generate();

        // Create token
        Token token = oAuth2Service.createToken(rsaJWK);

//        // Use token to request resource
        TestResource testResource = oAuth2Service.getTestResource(token.getAccessToken(), rsaJWK);

        log.info("-----RESPONSE FROM RESOURCE ENDPOINT-----");
        log.info(testResource.getStatus());
    }
}
