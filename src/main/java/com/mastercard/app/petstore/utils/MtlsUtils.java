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
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
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

    private final String mtlsKeyFilePath;
    private final String mtlsCrtFilePath;
    private final String mtlsKeystorePassword;
    private final String basePath;

    @Autowired
    public MtlsUtils(
            @Value("${mastercard.mtls.keyFile}") String mtlsKeyFilePath,
            @Value("${mastercard.mtls.crtFile}") String mtlsCrtFilePath,
            @Value("${mastercard.mtls.keyPassword}") String mtlsKeystorePassword,
            @Value("${mastercard.basePath}") String basePath
    ) {
        if (basePath.isEmpty()){
            throw new IllegalArgumentException("basePath in application.properties is empty");
        }
        this.basePath = basePath;
        if (mtlsKeyFilePath.isEmpty()){
            throw new IllegalArgumentException("keyFile in application-mtls.properties is empty");
        }
        this.mtlsKeyFilePath = mtlsKeyFilePath;
        if (mtlsCrtFilePath.isEmpty()){
            throw new IllegalArgumentException("crtFile in application-mtls.properties is empty");
        }
        this.mtlsCrtFilePath = mtlsCrtFilePath;
        if (mtlsKeystorePassword.isEmpty()){
            throw new IllegalArgumentException("keyPassword in application-mtls.properties is empty");
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

        KeyStore pkcs12KeyStore = loadKeyStore();

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(pkcs12KeyStore, mtlsKeystorePassword.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);
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
        return buildApiClientEncryption(fullBodyEncryptionConfig);
    }

    /**
     * Sets mTLS api client with field level encryption for adoptions.
     *
     * @param fieldLevelEncryptionConfigForAdoptions the config used to determine how field level encryption will work inside the api
     * @return the mTLS api client
     */
    @Bean
    public ApiClient apiClientEncryptionAdoptionFle(EncryptionConfig fieldLevelEncryptionConfigForAdoptions) throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, CertificateException, UnrecoverableKeyException {
        return buildApiClientEncryption(fieldLevelEncryptionConfigForAdoptions);
    }

    private ApiClient buildApiClientEncryption(EncryptionConfig config) throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, CertificateException, UnrecoverableKeyException {
        ApiClient client = new ApiClient();
        OkHttpClient.Builder httpClientBuilder = client.getHttpClient().newBuilder();
        client.setBasePath(basePath);
        client.setLenientOnJson(true);

        KeyStore pkcs12KeyStore = loadKeyStore();

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(pkcs12KeyStore, mtlsKeystorePassword.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());

        Interceptor encryptionInterceptor = new OkHttpJweInterceptor(config);

        client.setHttpClient(
                httpClientBuilder
                        .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagerFactory.getTrustManagers()[0])
                        .addInterceptor(encryptionInterceptor)
                        .build()
        );
        return client;
    }

    /**
     * Builds an in-memory key store from the PEM certificate and the encrypted PKCS#8 private key.
     */
    private KeyStore loadKeyStore() throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
        char[] password = mtlsKeystorePassword.toCharArray();
        Certificate certificate;
        try (InputStream in = new FileInputStream(mtlsCrtFilePath)) {
            certificate = CertificateFactory.getInstance("X.509").generateCertificate(in);
        }
        PrivateKey privateKey = readEncryptedPrivateKey(password);
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);
        keyStore.setKeyEntry("mtls", privateKey, password, new Certificate[]{certificate});
        return keyStore;
    }

    private PrivateKey readEncryptedPrivateKey(char[] password) throws IOException, NoSuchAlgorithmException {
        String pem = new String(Files.readAllBytes(Paths.get(mtlsKeyFilePath)), StandardCharsets.US_ASCII)
                .replaceAll("-----(BEGIN|END) ENCRYPTED PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        try {
            EncryptedPrivateKeyInfo encryptedInfo = new EncryptedPrivateKeyInfo(Base64.getDecoder().decode(pem));
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(encryptedInfo.getAlgName());
            Cipher cipher = Cipher.getInstance(encryptedInfo.getAlgName());
            cipher.init(Cipher.DECRYPT_MODE, secretKeyFactory.generateSecret(new PBEKeySpec(password)), encryptedInfo.getAlgParameters());
            PKCS8EncodedKeySpec keySpec = encryptedInfo.getKeySpec(cipher);
            for (String algorithm : new String[]{"EC", "RSA"}) {
                try {
                    return KeyFactory.getInstance(algorithm).generatePrivate(keySpec);
                } catch (InvalidKeySpecException ignored) {
                    // try next algorithm
                }
            }
            throw new IOException("Unsupported private key type in " + mtlsKeyFilePath);
        } catch (GeneralSecurityException e) {
            throw new IOException("Unable to decrypt private key " + mtlsKeyFilePath + ", check keyPassword", e);
        }
    }
}
