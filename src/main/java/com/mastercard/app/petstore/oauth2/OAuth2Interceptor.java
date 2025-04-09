package com.mastercard.app.petstore.oauth2;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.Date;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OAuth2Interceptor implements Interceptor {
    protected final PrivateKey privateSigningKey;
    protected final String keyAlias;
    protected final String keyId;
    protected final String audience;
    protected final String clientId;
    protected final String basepath;

    protected OAuth2Interceptor(PrivateKey privateSigningKey, String keyAlias, String keyId, String audience, String clientId, String basepath) {
        this.privateSigningKey = privateSigningKey;
        this.keyAlias = keyAlias;
        this.keyId = keyId;
        this.audience = audience;
        this.clientId = clientId;
        this.basepath = basepath;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        String dpopJwt;

        String reqUrl = builder.getUrl$okhttp().toString();
        String method = builder.getMethod$okhttp();
        // If the request is for a token we add private_key_jwt
        if (reqUrl.contains("oauth2/token")) {
            String privateJwt;
            try {
                privateJwt = generatePrivateJwt(method);
                builder.addHeader("client_assertion", privateJwt);
                builder.addHeader("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
                builder.addHeader("grant_type", "client_credentials");
            } catch (JOSEException e) {
                throw new RuntimeException(e);
            }
        }

        // We always add the DPoP header
        try {
            dpopJwt = generateDpopJwt(method, reqUrl);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        builder.addHeader("DPoP", dpopJwt);
        return chain.proceed(builder.build());
    }

    // Generate private key JWT
    // Signed with private key from MCD
    public String generatePrivateJwt(String audience) throws JOSEException {
        // Create RSA-signer with the private key
        RSASSASigner signer = new RSASSASigner(privateSigningKey);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(clientId)
                .jwtID(keyId)
                .audience(audience)
                .issuer(clientId)
                .issueTime(new Date())
                .expirationTime(new Date())
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.PS256) // Approved alg: https://confluence.mastercard.int/display/MAPI/2025+-+OAuth+2.0#id-2025OAuth2.0-JWTToken
                        .keyID(keyId)
                        .build(),
                claimsSet);

        signedJWT.sign(signer);

        // To serialize to compact form. (Same as JWE compact serialization)
        return signedJWT.serialize();
    }

    // Generate DPoP JWT
    // Signed with generated (transient) keys
    public String generateDpopJwt(String method, String audienceUrl) throws JOSEException {
        // Generate transient keys
        RSAKey rsaJWK = new RSAKeyGenerator(2048)
                .keyID(this.keyId)
                .generate();

        RSAKey rsaPublicJWK = rsaJWK.toPublicJWK();

        // Create RSA-signer with the private key
        JWSSigner signer = new RSASSASigner(rsaJWK);

        // Random string for the jwt id
        Random r = new Random();
        String randomString = Stream
                .generate(() -> String.valueOf(r.nextInt(9)))
                .limit(100)
                .collect(Collectors.joining());

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .jwtID(randomString)
                .claim("nonce", "nonce-value") // TODO are we doing this?
                .claim("htm", method)
                .audience(audienceUrl)
                .issueTime(new Date())
                .expirationTime(new Date())
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.PS256) // Approved alg: https://confluence.mastercard.int/display/MAPI/2025+-+OAuth+2.0#id-2025OAuth2.0-JWTToken
                        .jwk(rsaPublicJWK) // Transient public key in header
                        .type(JOSEObjectType.JWT)
                        .keyID(rsaJWK.getKeyID())
                        .build(),
                claimsSet);

        signedJWT.sign(signer);

        // To serialize to compact form. (Same as JWE compact serialization)
        return signedJWT.serialize();
    }
}
