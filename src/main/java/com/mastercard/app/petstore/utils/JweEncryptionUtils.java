package com.mastercard.app.petstore.utils;

import com.mastercard.developer.encryption.EncryptionConfig;
import com.mastercard.developer.encryption.EncryptionException;
import com.mastercard.developer.encryption.JweConfigBuilder;
import com.mastercard.developer.utils.EncryptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

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
     * @throws EncryptionException      the encryption exception. Will trigger if there's an issue with encryption
     * @throws GeneralSecurityException the general security exception. Covers other issues with security not covered by EncryptionException
     * @throws IOException              the io exception. Will trigger on fail to load files
     * @throws IllegalArgumentException the illegal argument exception. Will trigger when not all fields in applications.properties are set
     */
    @Bean
    public EncryptionConfig fieldLevelEncryptionConfigForEmployees() throws EncryptionException, GeneralSecurityException, IOException {
        checkProperties();
        Certificate encryptionCertificate = EncryptionUtils.loadEncryptionCertificate(encryptionCertificateFilePath);
        PrivateKey decryptionKey = EncryptionUtils.loadDecryptionKey(decryptionKeyFilePath, decryptionKeyAlias, decryptionKeyPassword);

        return JweConfigBuilder.aJweEncryptionConfig()
                .withEncryptionCertificate(encryptionCertificate)
                .withDecryptionKey(decryptionKey)
                .withEncryptionPath("$.ssn","$")
                .withDecryptionPath("$.encryptedSsn.encryptedData","$.ssn")
                .withDecryptionPath("$.encryptedFirstName.encryptedData","$.firstName")
                .withDecryptionPath("$.encryptedLastName.encryptedData","$.lastName")
                .withDecryptionPath("$.encryptedPhoneNumber.encryptedData","$.phoneNumber")
                .withDecryptionPath("$.encryptedEmail.encryptedData","$.email")
                .withDecryptionPath("$.encryptedUsername.encryptedData","$.username")
                .withDecryptionPath("$.encryptedAccountStatus.encryptedData","$.accountStatus")
                .build();
    }

    /**
     * Sets field level encryption config for adoptions.
     *
     * @return the field level encryption config for adoptions
     * @throws EncryptionException      the encryption exception. Will trigger if there's an issue with encryption
     * @throws GeneralSecurityException the general security exception. Covers other issues with security not covered by EncryptionException
     * @throws IOException              the io exception. Will trigger on fail to load files
     * @throws IllegalArgumentException the illegal argument exception. Will trigger when not all fields in applications.properties are set
     */
    @Bean
    public EncryptionConfig fieldLevelEncryptionConfigForAdoptions() throws EncryptionException, GeneralSecurityException, IOException {
        checkProperties();
        Certificate encryptionCertificate = EncryptionUtils.loadEncryptionCertificate(encryptionCertificateFilePath);
        PrivateKey decryptionKey = EncryptionUtils.loadDecryptionKey(decryptionKeyFilePath, decryptionKeyAlias, decryptionKeyPassword);

        return JweConfigBuilder.aJweEncryptionConfig()
                .withEncryptionCertificate(encryptionCertificate)
                .withDecryptionKey(decryptionKey)
                .withEncryptionPath("$.owner","$.encryptedOwner")
                .withDecryptionPath("$.encryptedOwner.encryptedData","$.owner")
                .build();
    }

    /**
     * Sets full body encryption config. The entire payload will be encrypted
     *
     * @return the full body encryption config
     * @throws EncryptionException      the encryption exception. Will trigger if there's an issue with encryption
     * @throws GeneralSecurityException the general security exception. Covers other issues with security not covered by EncryptionException
     * @throws IOException              the io exception. Will trigger on fail to load files
     * @throws IllegalArgumentException the illegal argument exception. Will trigger when not all fields in applications.properties are set
     */
    @Bean
    public EncryptionConfig fullBodyEncryptionConfig() throws EncryptionException, GeneralSecurityException, IOException {
        checkProperties();
        Certificate encryptionCertificate = EncryptionUtils.loadEncryptionCertificate(encryptionCertificateFilePath);
        PrivateKey decryptionKey = EncryptionUtils.loadDecryptionKey(decryptionKeyFilePath, decryptionKeyAlias, decryptionKeyPassword);

        return JweConfigBuilder.aJweEncryptionConfig()
                .withEncryptionCertificate(encryptionCertificate)
                .withEncryptionPath("$", "$")
                .withDecryptionKey(decryptionKey)
                .build();
    }

    private void checkProperties() {
        if (encryptionCertificateFilePath.isEmpty()){
            throw new IllegalArgumentException("encryptionCert in application.properties is empty");
        }
        if (decryptionKeyFilePath.isEmpty()){
            throw new IllegalArgumentException("decryptionKeys in application.properties is empty");
        }
        if (decryptionKeyAlias.isEmpty()){
            throw new IllegalArgumentException("decryptionKeyAlias in application.properties is empty");
        }
        if (decryptionKeyPassword.isEmpty()){
            throw new IllegalArgumentException("decryptionKeyPassword in application.properties is empty");
        }
    }
}
