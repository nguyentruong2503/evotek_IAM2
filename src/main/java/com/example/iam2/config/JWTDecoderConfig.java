package com.example.iam2.config;

import com.example.iam2.service.impl.JWTServiceImpl;
import com.example.iam2.util.RsaKeyLoader;
import com.nimbusds.jose.JOSEException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPublicKey;
import java.util.Objects;

@Component
@Slf4j
public class JWTDecoderConfig implements JwtDecoder {

    @Autowired
    private JWTServiceImpl jwtService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Autowired
    private RsaKeyLoader rsaKeyLoader;

    @Value("${jwt.public-key}")
    private Resource publicKeyResource;

    @Override
    public Jwt decode(String token) {
        try {
            if (!jwtService.verifyToken(token)) {
                throw new RuntimeException("Invalid token");
            }

            if (Objects.isNull(nimbusJwtDecoder)) {
                RSAPublicKey publicKey = rsaKeyLoader.loadPublicKey(publicKeyResource);
                nimbusJwtDecoder = NimbusJwtDecoder.withPublicKey(publicKey)
                        .signatureAlgorithm(SignatureAlgorithm.RS256)
                        .build();
            }
            return nimbusJwtDecoder.decode(token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

