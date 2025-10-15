package com.mastercard.app.petstore.utils;

import com.mastercard.developer.oauth2.config.Configuration;
import com.mastercard.developer.oauth2.interceptors.OkHttp3ClientCredentialInterceptor;
import com.mastercard.developer.oauth2.keygenerators.DefaultKeyGenerator;
import com.mastercard.developer.oauth2.utils.ConsoleLogger;
import com.mastercard.developer.utils.AuthenticationUtils;
import org.openapitools.client.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.security.InvalidAlgorithmParameterException;
import java.security.PrivateKey;
import java.util.logging.Logger;

@Profile({"oauth2stage", "oauth2onlystage"})
@org.springframework.context.annotation.Configuration
public class OAuth2Utils {

    private final String signingKeyContainer;
    private final String signingKeyAlias;
    private final String signingKeyPassword;
    private final String basePath;
    private final String tokenUrl;
    private final String keyId;
    private final String clientId;
    private final String scope;
    private final String dpopKeyType;

    @Autowired
    public OAuth2Utils(
            @Value("${mastercard.oauth2.pkcs12KeyFile}") String signingKeyContainer,
            @Value("${mastercard.oauth2.keyAlias}") String signingKeyAlias,
            @Value("${mastercard.oauth2.keyPassword}") String signingKeyPassword,
            @Value("${mastercard.basePath}") String basePath,
            @Value("${mastercard.oauth2.tokenUrl}") String tokentUri,
            @Value("${mastercard.oauth2.keyId}") String keyId,
            @Value("${mastercard.oauth2.clientId}") String clientId,
            @Value("${mastercard.oauth2.scope}") String scope,
            @Value("${mastercard.oauth2.dpopKeyType}") String dpopSigningAlgorithm) {

        if (isNullOrEmpty(basePath)) {
            throw new IllegalArgumentException("basePath in application.properties is empty");
        }
        this.basePath = basePath;
        if (isNullOrEmpty(signingKeyContainer)) {
            throw new IllegalArgumentException("pkcs12KeyFile in application-oauth2.properties is empty");
        }
        this.signingKeyContainer = signingKeyContainer;
        if (isNullOrEmpty(clientId)) {
            throw new IllegalArgumentException("client id in application-oauth2.properties is empty");
        }
        this.clientId = clientId;
        if (isNullOrEmpty(signingKeyAlias)) {
            throw new IllegalArgumentException("keyAlias in application-oauth2.properties is empty");
        }
        this.signingKeyAlias = signingKeyAlias;
        if (isNullOrEmpty(signingKeyPassword)) {
            throw new IllegalArgumentException("keyPassword in application-oauth2.properties is empty");
        }
        this.signingKeyPassword = signingKeyPassword;

        if (isNullOrEmpty(tokentUri)) {
            throw new IllegalArgumentException("tokentUri in application-oauth2.properties is empty");
        }
        this.tokenUrl = tokentUri;


        if (isNullOrEmpty(keyId)) {
            throw new IllegalArgumentException("keyId in application-oauth2.properties is empty");
        }

        this.keyId = keyId;
        if (isNullOrEmpty(scope)) {
            throw new IllegalArgumentException("scope in application-oauth2.properties is empty");
        }
        this.scope = scope;
        if (isNullOrEmpty(dpopSigningAlgorithm)) {
            this.dpopKeyType = "ES256";
        } else {
            this.dpopKeyType = dpopSigningAlgorithm;
        }
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
    /**
     * Sets an oAuth api client without encryption. This will be used to send authenticated requests to the server.
     *
     * @return the oAuth api client
     */
    @Bean
    public ApiClient apiClient() {
        ApiClient client = newGenericClient();
        var config = new Configuration.Builder()
                .withClientId(clientId)
                .withSigningKeyId(keyId)
                .withTokenUrl(tokenUrl)
                .withScope(scope)
                .withSigningKey(getSigningKey())
                .withJwtExpirationSeconds(840) // 2 minutes for production
                .withKeyAlgorithmValidation(true)
                .withPrivateJwtAlgorithm("PS256")
                .withNbfDelay(120)
                .withJtiLength(16)
                //.withLogger(new ConsoleLogger())
                .withKeyGenerator(new DefaultKeyGenerator.Builder()
                        .withDpopKeyLifeTimeInSeconds(3600)
                        .withDpopAlgorithm("ES256").build())
                .build();

        client.setHttpClient(
                client.getHttpClient()
                        .newBuilder()
                        .addInterceptor(
                                new OkHttp3ClientCredentialInterceptor(config))
                        .build()
        );

        return client;
    }

    private PrivateKey getSigningKey() {
        PrivateKey signingKey = null;
        try {
            signingKey = AuthenticationUtils.loadSigningKey(signingKeyContainer, signingKeyAlias, signingKeyPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signingKey;
    }

    private ApiClient newGenericClient() {
        ApiClient client = new ApiClient();
        client.setLenientOnJson(true);
        client.setBasePath(basePath);
        client.setDebugging(false);
        return client;
    }
}
