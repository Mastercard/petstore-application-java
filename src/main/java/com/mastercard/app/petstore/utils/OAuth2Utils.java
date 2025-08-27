package com.mastercard.app.petstore.utils;

import com.mastercard.developer.interceptors.OkHttpJweInterceptor;
import com.mastercard.developer.oauth2.interceptors.OkHttp3OAuth2Interceptor;
import com.mastercard.developer.utils.AuthenticationUtils;
import okhttp3.Interceptor;
import org.openapitools.client.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.security.PrivateKey;

@Profile({ "oauth2stage"})
@org.springframework.context.annotation.Configuration
public class OAuth2Utils {

    private final String signingKeyContainer;
    private final String signingKeyAlias;
    private final String signingKeyPassword;
    private final String basePath;
    private final String tokenUrl;
    private final String audience ;
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
            @Value("${mastercard.oauth2.audience}") String audience,
            @Value("${mastercard.oauth2.tokenUrl}") String tokentUrl,
            @Value("${mastercard.oauth2.keyId}") String keyId,
            @Value("${mastercard.oauth2.clientId}") String clientId,
            @Value("${mastercard.oauth2.scope}") String scope,
            @Value("${mastercard.oauth2.dpopKeyType}") String dpopKeyType) {

        if (basePath.isEmpty()){
            throw new IllegalArgumentException("basePath in application.properties is empty");
        }
        this.basePath = basePath;
        if (signingKeyContainer.isEmpty()){
            throw new IllegalArgumentException("pkcs12KeyFile in application-oauth2.properties is empty");
        }
        this.signingKeyContainer = signingKeyContainer;
        if (clientId.isEmpty()){
            throw new IllegalArgumentException("client id  in application-oauth2.properties is empty");
        }
        this.clientId = clientId;
        if (signingKeyAlias.isEmpty()){
            throw new IllegalArgumentException("keyAlias in application-oauth2.properties is empty");
        }
        this.signingKeyAlias = signingKeyAlias;
        if (signingKeyPassword.isEmpty()){
            throw new IllegalArgumentException("keyPassword in application-oauth2.properties is empty");
        }
        this.signingKeyPassword = signingKeyPassword;

        if(tokentUrl.isEmpty()) {
            throw new IllegalArgumentException("tokentUrl in application-oauth2.properties is empty");
        }
        this.tokenUrl = tokentUrl;
        if(audience.isEmpty()) {
            throw new IllegalArgumentException("audience in application-oauth2.properties is empty");
        }
        this.audience = audience;

        if(keyId.isEmpty()) {
            throw new IllegalArgumentException("keyId in application-oauth2.properties is empty");
        }

        this.keyId = keyId;
        if (scope.isEmpty()) {
            throw new IllegalArgumentException("scope in application-oauth2.properties is empty");
        }
        this.scope = scope;
        if (dpopKeyType.isEmpty()) {
            this.dpopKeyType = "PS256";
        }
        else {
            this.dpopKeyType = dpopKeyType;
        }
    }

    /**
     * Sets an oAuth api client without encryption. This will be used to send authenticated requests to the server.
     *
     * @return the oAuth api client
     */
    @Bean
    public ApiClient apiClient() {
        ApiClient client = newGenericClient();
        client.setHttpClient(
                client.getHttpClient()
                        .newBuilder()
                        .addInterceptor(new OkHttp3OAuth2Interceptor(clientId, getSigningKey(), tokenUrl, audience, keyId, scope, dpopKeyType))
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
