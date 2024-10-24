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
import org.springframework.stereotype.Component;

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
        this.signingKeyContainer = signingKeyContainer;
        this.signingKeyAlias = signingKeyAlias;
        this.signingKeyPassword = signingKeyPassword;
        this.consumerKey = consumerKey;
        this.basePath = basePath;
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
     * @param config the encryption config
     * @return the oAuth api client
     */
    @Bean
    public ApiClient apiClientEncryption(EncryptionConfig fullBodyEncryptionConfig) {
        Interceptor encryptionInterceptor;
        if (fullBodyEncryptionConfig.getScheme() == EncryptionConfig.Scheme.JWE) {
            encryptionInterceptor = new OkHttpJweInterceptor(fullBodyEncryptionConfig);
        } else {
            encryptionInterceptor = new OkHttpFieldLevelEncryptionInterceptor(fullBodyEncryptionConfig);
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
        client.setBasePath(basePath);
        client.setDebugging(false);
        return client;
    }
}
