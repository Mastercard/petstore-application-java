package com.mastercard.app.petstore.utils;

import com.mastercard.developer.encryption.EncryptionConfig;
import com.mastercard.developer.encryption.JweConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openapitools.client.ApiClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MtlsUtilsTest {

    private static final String KEY = "src/main/resources/MTLS/testmtlsrefservice1-production.key";
    private static final String CRT = "src/main/resources/MTLS/testmtlsrefservice1-production.crt";
    private static final String PASSWORD = "TestMTLSRefService1";
    private static final String BASE_PATH = "https://localhost/petstore";

    private final EncryptionConfig encryptionConfig = Mockito.mock(JweConfig.class);

    private MtlsUtils utils() {
        return new MtlsUtils(KEY, CRT, PASSWORD, BASE_PATH);
    }

    @Test
    void constructorRejectsEmptyBasePath() {
        assertThrows(IllegalArgumentException.class, () -> new MtlsUtils(KEY, CRT, PASSWORD, ""));
    }

    @Test
    void constructorRejectsEmptyKeyFile() {
        assertThrows(IllegalArgumentException.class, () -> new MtlsUtils("", CRT, PASSWORD, BASE_PATH));
    }

    @Test
    void constructorRejectsEmptyCrtFile() {
        assertThrows(IllegalArgumentException.class, () -> new MtlsUtils(KEY, "", PASSWORD, BASE_PATH));
    }

    @Test
    void constructorRejectsEmptyPassword() {
        assertThrows(IllegalArgumentException.class, () -> new MtlsUtils(KEY, CRT, "", BASE_PATH));
    }

    @Test
    void apiClientUsesBasePath() throws Exception {
        ApiClient client = utils().apiClient();
        assertNotNull(client);
        assertEquals(BASE_PATH, client.getBasePath());
    }

    @Test
    void apiClientEncryptionAddsInterceptor() throws Exception {
        MtlsUtils utils = utils();
        int plain = utils.apiClient().getHttpClient().interceptors().size();
        ApiClient client = utils.apiClientEncryption(encryptionConfig);
        assertEquals(BASE_PATH, client.getBasePath());
        assertEquals(plain + 1, client.getHttpClient().interceptors().size());
    }

    @Test
    void apiClientEncryptionAdoptionFleAddsInterceptor() throws Exception {
        MtlsUtils utils = utils();
        int plain = utils.apiClient().getHttpClient().interceptors().size();
        ApiClient client = utils.apiClientEncryptionAdoptionFle(encryptionConfig);
        assertEquals(plain + 1, client.getHttpClient().interceptors().size());
    }

    @Test
    void wrongPasswordFails() {
        IOException e = assertThrows(IOException.class,
                () -> new MtlsUtils(KEY, CRT, "wrong", BASE_PATH).apiClient());
        assertTrue(e.getMessage().contains("check keyPassword"));
    }

    @Test
    void missingKeyFileFails() {
        assertThrows(IOException.class,
                () -> new MtlsUtils("missing.key", CRT, PASSWORD, BASE_PATH).apiClient());
    }

    @Test
    void missingCrtFileFails() {
        assertThrows(IOException.class,
                () -> new MtlsUtils(KEY, "missing.crt", PASSWORD, BASE_PATH).apiClient());
    }
}
