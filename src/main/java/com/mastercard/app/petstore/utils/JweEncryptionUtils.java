package com.mastercard.app.petstore.utils;

import com.mastercard.developer.encryption.EncryptionConfig;
import com.mastercard.developer.encryption.EncryptionException;
import com.mastercard.developer.encryption.JweConfigBuilder;
import com.mastercard.developer.utils.EncryptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;

@org.springframework.context.annotation.Configuration
public class JweEncryptionUtils {

    @Value("${mastercard.encryption.encryptionCert}")
    private String encryptionCertificateFilePath;
    @Value("${mastercard.encryption.decryptionKeys}")
    private String decryptionKeyFilePath;
    @Value("${mastercard.encryption.decryptionKeyAlias}")
    private String decryptionKeyAlias;
    @Value("${mastercard.encryption.decryptionKeyPassword}")
    private String decryptionKeyPassword;

    /**
     * Sets field level encryption config for employees.
     *
     * @return the field level encryption config for employees
     * @throws EncryptionException      the encryption exception. Will trigger if keys fail to get extracted
     * @throws GeneralSecurityException the general security exception
     * @throws IOException              the io exception
     */
    @Bean
    public EncryptionConfig fieldLevelEncryptionConfigForEmployees() throws EncryptionException, GeneralSecurityException, IOException {
        Certificate encryptionCertificate = EncryptionUtils.loadEncryptionCertificate(encryptionCertificateFilePath);
        PrivateKey decryptionKey = EncryptionUtils.loadDecryptionKey(decryptionKeyFilePath, decryptionKeyAlias, decryptionKeyPassword);

        return JweConfigBuilder.aJweEncryptionConfig()
                .withEncryptionCertificate(encryptionCertificate)
                .withDecryptionKey(decryptionKey)
                .withEncryptionPath("$.ssn","$")
                .withDecryptionPath("$.encryptedSsn","$.ssn")
                .withDecryptionPath("$.encryptedFirstName","$.firstName")
                .withDecryptionPath("$.encryptedLastName","$.lastName")
                .withDecryptionPath("$.encryptedPhoneNumber","$.phoneNumber")
                .withDecryptionPath("$.encryptedEmail","$.email")
                .withDecryptionPath("$.encryptedUsername","$.username")
                .build();
    }

    /**
     * Sets field level encryption config for adoptions.
     *
     * @return the field level encryption config for adoptions
     * @throws EncryptionException      the encryption exception. Will trigger if keys fail to get extracted
     * @throws GeneralSecurityException the general security exception
     * @throws IOException              the io exception
     */
    @Bean
    public EncryptionConfig fieldLevelEncryptionConfigForAdoptions() throws EncryptionException, GeneralSecurityException, IOException {
        Certificate encryptionCertificate = EncryptionUtils.loadEncryptionCertificate(encryptionCertificateFilePath);
        PrivateKey decryptionKey = EncryptionUtils.loadDecryptionKey(decryptionKeyFilePath, decryptionKeyAlias, decryptionKeyPassword);

        return JweConfigBuilder.aJweEncryptionConfig()
                .withEncryptionCertificate(encryptionCertificate)
                .withDecryptionKey(decryptionKey)
                .withEncryptionPath("$.owner","$.encryptedOwner")
                .withDecryptionPath("$.encryptedOwner","$.owner")
                .build();
    }

    /**
     * Sets full body encryption config. The entire payload will be encrypted
     *
     * @return the full body encryption config
     * @throws EncryptionException      the encryption exception
     * @throws GeneralSecurityException the general security exception
     * @throws IOException              the io exception
     */
    @Bean
    public EncryptionConfig fullBodyEncryptionConfig() throws EncryptionException, GeneralSecurityException, IOException {
        Certificate encryptionCertificate = EncryptionUtils.loadEncryptionCertificate(encryptionCertificateFilePath);
        PrivateKey decryptionKey = EncryptionUtils.loadDecryptionKey(decryptionKeyFilePath, decryptionKeyAlias, decryptionKeyPassword);

        return JweConfigBuilder.aJweEncryptionConfig()
                .withEncryptionCertificate(encryptionCertificate)
                .withEncryptionPath("$", "$")
                .withDecryptionKey(decryptionKey)
                .build();
    }

}
