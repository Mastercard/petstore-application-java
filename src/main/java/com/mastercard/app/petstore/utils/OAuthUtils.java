package com.mastercard.app.petstore.utils;

import com.mastercard.developer.encryption.EncryptionConfig;
import com.mastercard.developer.interceptors.OkHttpFieldLevelEncryptionInterceptor;
import com.mastercard.developer.interceptors.OkHttpJweInterceptor;
import com.mastercard.developer.interceptors.OkHttpOAuth1Interceptor;
import com.mastercard.developer.utils.AuthenticationUtils;
import okhttp3.Interceptor;
import org.openapitools.client.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.security.PrivateKey;

@Profile({"oauth"})
@org.springframework.context.annotation.Configuration
public class OAuthUtils {

    private final String signingKeyContainer;
    private final String signingKeyAlias;
    private final String signingKeyPassword;
    private final String consumerKey;
    private final String basePath;

    @Autowired
    public OAuthUtils(
            @Value("${mastercard.oauth.pkcs12KeyFile}") String signingKeyContainer,
            @Value("${mastercard.oauth.keyAlias}") String signingKeyAlias,
            @Value("${mastercard.oauth.keyPassword}") String signingKeyPassword,
            @Value("${mastercard.oauth.consumerKey}") String consumerKey,
            @Value("${mastercard.basePath}") String basePath
    ) {
        if (basePath.isEmpty()){
            throw new IllegalArgumentException("basePath in application.properties is empty");
        }
        this.basePath = basePath;
        if (signingKeyContainer.isEmpty()){
            throw new IllegalArgumentException("pkcs12KeyFile in application-oauth.properties is empty");
        }
        this.signingKeyContainer = signingKeyContainer;
        if (consumerKey.isEmpty()){
            throw new IllegalArgumentException("consumerKey in application-oauth.properties is empty");
        }
        this.consumerKey = consumerKey;
        if (signingKeyAlias.isEmpty()){
            throw new IllegalArgumentException("keyAlias in application.properties is empty");
        }
        this.signingKeyAlias = signingKeyAlias;
        if (signingKeyPassword.isEmpty()){
            throw new IllegalArgumentException("keyPassword in application.properties is empty");
        }
        this.signingKeyPassword = signingKeyPassword;
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
                        .addInterceptor(new OkHttpOAuth1Interceptor(consumerKey, getSigningKey()))
                        .build()
        );
        return client;
    }

    /**
     * Sets an oAuth api client with encryption. This will be used to send authenticated requests to the server.
     *
     * @param  fullBodyEncryptionConfig the config used to determine how encryption will work inside the api
     * @return the oAuth api client
     */
    @Bean
    public ApiClient apiClientEncryption(EncryptionConfig fullBodyEncryptionConfig) {
        return buildApiClientEncryption(fullBodyEncryptionConfig);
    }

    @Bean
    public ApiClient apiClientEncryptionAdoptionFle(EncryptionConfig fieldLevelEncryptionConfigForAdoptions) {
        return buildApiClientEncryption(fieldLevelEncryptionConfigForAdoptions);
    }

    @Bean
    public ApiClient apiClientEncryptionEmployeeFle(EncryptionConfig fieldLevelEncryptionConfigForEmployees) {
        return buildApiClientEncryption(fieldLevelEncryptionConfigForEmployees);
    }

    private ApiClient buildApiClientEncryption(EncryptionConfig config){
        Interceptor encryptionInterceptor;
        if (config.getScheme() == EncryptionConfig.Scheme.JWE) {
            encryptionInterceptor = new OkHttpJweInterceptor(config);
        } else {
            encryptionInterceptor = new OkHttpFieldLevelEncryptionInterceptor(config);
        }

        ApiClient client = newGenericClient();
        client.setHttpClient(
                client.getHttpClient()
                        .newBuilder()
                        .addInterceptor(encryptionInterceptor)
                        .addInterceptor(new OkHttpOAuth1Interceptor(consumerKey, getSigningKey()))
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
