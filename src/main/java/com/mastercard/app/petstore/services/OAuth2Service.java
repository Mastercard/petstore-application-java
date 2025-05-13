package com.mastercard.app.petstore.services;

import com.mastercard.developer.utils.AuthenticationUtils;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.codec.binary.Base64;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.AuthenticationApi;
import org.openapitools.client.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Date;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Employee service.
 */
@Service
public class OAuth2Service {
    private final AuthenticationApi authenticationApi;

    // Statics
    protected final String privateSigningKey = "/Users/e069279/IdeaProjects/petstore-application-java/src/main/resources/oauth2/petstore-sandbox-signing.p12";
    protected final String keyAlias = "keyalias";
    protected final String keyPassword = "keystorepassword!";
    protected final String keyId = "1";
    protected final String audience = "http://localhost:3000";
    protected final String clientId = "RsBmw5bcdEqzmSJGVUkKCnwQccp8ipsQBcN3H8q94aebab31";
    protected final String basepath = "http://localhost:3000/oidc";

    @Autowired
    public OAuth2Service(AuthenticationApi authenticationApi) {
        this.authenticationApi = authenticationApi;
    }

    // Create a new OAuth2 token
    public Token createToken(RSAKey key) throws ApiException, JOSEException, NoSuchAlgorithmException {
        // Use the same key.
        return authenticationApi.createToken(
                generateDpopJwt("POST", audience, "http://localhost:3000/oidc/token", null, key),
                "RsBmw5bcdEqzmSJGVUkKCnwQccp8ipsQBcN3H8q94aebab31",
                generatePrivateJwt(audience),
                "client_credentials",
                "urn:ietf:params:oauth:client-assertion-type:jwt-bearer"
        );
    }

    // Use OAuth2 token to get a test resource
    public TestResource getTestResource(String auth, RSAKey key) throws ApiException, JOSEException, NoSuchAlgorithmException {
        return authenticationApi.getTestResource(
            "DPoP " + auth,
            clientId,
            "none",
            generateDpopJwt("POST", audience,"http://localhost:3000/oidc/me", auth, key)
        );
    }

    // Generate private key JWT
    // Signed with private key from MCD
    public String generatePrivateJwt(String audience) throws JOSEException {
        // Create RSA-signer with the private key
        RSASSASigner signer = new RSASSASigner(getSigningKey());
        Date now = new Date();
        now.setTime(System.currentTimeMillis() + 200000);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("RsBmw5bcdEqzmSJGVUkKCnwQccp8ipsQBcN3H8q94aebab31")
                .jwtID(keyId)
                .audience(audience)
                .issuer(clientId)
                .issueTime(new Date())
                .expirationTime(now)
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256) // Approved alg: https://confluence.mastercard.int/display/MAPI/2025+-+OAuth+2.0#id-2025OAuth2.0-JWTToken
                        .keyID(keyId)
                        .build(),
                claimsSet);

        signedJWT.sign(signer);

        // To serialize to compact form. (Same as JWE compact serialization)
        return signedJWT.serialize();
    }

    // Generate DPoP JWT
    // Signed with generated (transient) keys
    public String generateDpopJwt(String method, String audienceUrl, String htu, String ath, RSAKey rsaJWK) throws JOSEException, NoSuchAlgorithmException {
        RSAKey rsaPublicJWK = rsaJWK.toPublicJWK();

        // Create RSA-signer with the private key
        JWSSigner signer = new RSASSASigner(rsaJWK);

        // Random string for the jwt id
        Random r = new Random();
        String randomString = Stream
                .generate(() -> String.valueOf(r.nextInt(9)))
                .limit(100)
                .collect(Collectors.joining());

        Date exp = new Date();
        exp.setTime(System.currentTimeMillis() + 200000);

        JWTClaimsSet claimsSet;

        if (ath != null) {
            claimsSet = new JWTClaimsSet.Builder()
                    .jwtID(randomString)
                    .claim("ath", encode(ath))
//                .claim("nonce", "nonce-value") // TODO are we doing this?
                    .claim("htm", method)
                    .claim("htu", htu)
                    .audience(audienceUrl)
                    .issueTime(new Date())
                    .expirationTime(exp)
                    .build();
        } else {
            claimsSet = new JWTClaimsSet.Builder()
                    .jwtID(randomString)
//                .claim("nonce", "nonce-value") // TODO are we doing this?
                    .claim("htm", method)
                    .claim("htu", htu)
                    .audience(audienceUrl)
                    .issueTime(new Date())
                    .expirationTime(exp)
                    .build();
        }

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.PS256) // Approved alg: https://confluence.mastercard.int/display/MAPI/2025+-+OAuth+2.0#id-2025OAuth2.0-JWTToken
                        .jwk(rsaPublicJWK) // Transient public key in header
                        .type(new JOSEObjectType("dpop+jwt"))
                        .keyID("1")
                        .build(),
                claimsSet);

        signedJWT.sign(signer);

        // To serialize to compact form. (Same as JWE compact serialization)
        return signedJWT.serialize();
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

    private static String encode(final String clearText) throws NoSuchAlgorithmException {
        return new String(
                Base64.encodeBase64URLSafe(MessageDigest.getInstance("SHA-256").digest(clearText.getBytes(StandardCharsets.UTF_8))));
    }
}
