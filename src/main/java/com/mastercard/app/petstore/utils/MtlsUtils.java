package com.mastercard.app.petstore.utils;

import com.mastercard.developer.encryption.EncryptionConfig;
import com.mastercard.developer.interceptors.OkHttpJweInterceptor;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.openapitools.client.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@Profile({"mtls"})
@Component
public class MtlsUtils {

    private final String mtlsPkcs12KeyFilePath;
    private final String mtlsKeystorePassword;
    private final String basePath;

    @Autowired
    public MtlsUtils(
            @Value("${mastercard.mtls.pfxKeyFile}") String mtlsPkcs12KeyFilePath,
            @Value("${mastercard.mtls.keyPassword}") String mtlsKeystorePassword,
            @Value("${mastercard.basePath}") String basePath
    ) {
        if (basePath.isEmpty()){
            throw new IllegalArgumentException("basePath in application.properties is empty");
        }
        this.basePath = basePath;
        if (mtlsPkcs12KeyFilePath.isEmpty()){
            throw new IllegalArgumentException("pfxKeyFile in application-mtls.properties is empty");
        }
        this.mtlsPkcs12KeyFilePath = mtlsPkcs12KeyFilePath;
        if (mtlsKeystorePassword.isEmpty()){
            throw new IllegalArgumentException("keyPassword in application.properties is empty");
        }
        this.mtlsKeystorePassword = mtlsKeystorePassword;
    }

    /**
     * Sets mTLS api client. This will be used to send authenticated requests to the server.
     *
     * @return the mTLS api client
     * @throws IOException               the io exception. Will trigger on fail to load file
     * @throws KeyStoreException         the key store exception. Will trigger on failure to extract keys, typically through bad password
     * @throws NoSuchAlgorithmException  the no such algorithm exception. Will trigger is specified algorithm is not found
     * @throws KeyManagementException    the key management exception. Will trigger when not able to use keys correctly
     * @throws CertificateException      the certificate exception. Will trigger when not able to use cert correctly
     * @throws UnrecoverableKeyException the unrecoverable key exception. Will trigger when keys are corrupted
     */
    @Bean
    public ApiClient apiClient() throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, CertificateException, UnrecoverableKeyException {
        ApiClient client = new ApiClient();
        OkHttpClient.Builder httpClientBuilder = client.getHttpClient().newBuilder();
        client.setBasePath(basePath);
        client.setLenientOnJson(true);

        KeyStore pkcs12KeyStore = KeyStore.getInstance("PKCS12");
        pkcs12KeyStore.load(new FileInputStream(mtlsPkcs12KeyFilePath), mtlsKeystorePassword.toCharArray());

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(pkcs12KeyStore, mtlsKeystorePassword.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(pkcs12KeyStore);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());
        client.setHttpClient(
                httpClientBuilder
                        .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagerFactory.getTrustManagers()[0])
                        .build()
        );
        return client;
    }

    /**
     * Sets mTLS api client with encryption. This will be used to send authenticated requests to the server.
     *
     * @param fullBodyEncryptionConfig the config used to determine how encryption will work inside the api
     * @return the mTLS api client
     * @throws IOException               the io exception. Will trigger on fail to load file
     * @throws KeyStoreException         the key store exception. Will trigger on failure to extract keys, typically through bad password
     * @throws NoSuchAlgorithmException  the no such algorithm exception. Will trigger is specified algorithm is not found
     * @throws KeyManagementException    the key management exception. Will trigger when not able to use keys correctly
     * @throws CertificateException      the certificate exception. Will trigger when not able to use cert correctly
     * @throws UnrecoverableKeyException the unrecoverable key exception. Will trigger when keys are corrupted
     */
    @Bean
    public ApiClient apiClientEncryption(EncryptionConfig fullBodyEncryptionConfig) throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, CertificateException, UnrecoverableKeyException {
        ApiClient client = new ApiClient();
        OkHttpClient.Builder httpClientBuilder = client.getHttpClient().newBuilder();
        client.setBasePath(basePath);
        client.setLenientOnJson(true);

        KeyStore pkcs12KeyStore = KeyStore.getInstance("PKCS12");
        pkcs12KeyStore.load(new FileInputStream(mtlsPkcs12KeyFilePath), mtlsKeystorePassword.toCharArray());

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(pkcs12KeyStore, mtlsKeystorePassword.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(pkcs12KeyStore);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());

        Interceptor encryptionInterceptor = new OkHttpJweInterceptor(fullBodyEncryptionConfig);

        client.setHttpClient(
                httpClientBuilder
                        .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagerFactory.getTrustManagers()[0])
                        .addInterceptor(encryptionInterceptor)
                        .build()
        );
        return client;
    }
}
