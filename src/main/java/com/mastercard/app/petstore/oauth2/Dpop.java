package com.mastercard.app.petstore.oauth2;

import com.mastercard.developer.utils.AuthenticationUtils;
import org.openapitools.client.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import java.security.PrivateKey;

@Profile({"oauth2"})
@org.springframework.context.annotation.Configuration
public class Dpop {

    // JWT CLAIM INFORMATION FROM CLIENT
    private final String privateSigningKey;
    private final String keyAlias;
    private final String keyPassword;
    private final String keyId;
    private final String audience;
    private final String clientId;
    private final String basepath;

    @Autowired
    public Dpop(
            @Value("${mastercard.oauth2.privateSigningKey}") String privateSigningKey,
            @Value("${mastercard.oauth2.keyId}") String keyId,
            @Value("${mastercard.oauth2.audience}") String audience,
            @Value("${mastercard.oauth2.keyAlias}") String keyAlias,
            @Value("${mastercard.oauth2.keyPassword}") String keyPassword,
            @Value("${mastercard.oauth2.clientId}") String clientId,
            @Value("${mastercard.oauth2.basepath}") String basepath
    ) {
        this.privateSigningKey = privateSigningKey;
        this.keyAlias = keyAlias;
        this.keyPassword = keyPassword;
        this.keyId = keyId;
        this.audience = audience;
        this.clientId = clientId;
        this.basepath = basepath;
    }

    /**
     * Sets an oAuth api client without encryption. This will be used to send authenticated requests to the server.
     *
     * @return the oAuth api client
     */
    @Bean
    public ApiClient apiClient() {
        // Create an instantiate API client
        ApiClient client = new ApiClient();
        client.setLenientOnJson(true);
        client.setBasePath(basepath);
        client.setDebugging(false);

        client.setHttpClient(
                client.getHttpClient()
                        .newBuilder()
                        .addInterceptor(new OAuth2Interceptor(getSigningKey(), keyAlias, keyId, audience, clientId, basepath))
                        .build()
        );
        return client;
    }

    private PrivateKey getSigningKey() {
        PrivateKey signingKey = null;
        try {
            signingKey = AuthenticationUtils.loadSigningKey(privateSigningKey, keyAlias, keyPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signingKey;
    }
}