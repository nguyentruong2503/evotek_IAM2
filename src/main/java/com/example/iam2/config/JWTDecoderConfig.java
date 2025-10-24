package com.example.iam2.config;

import com.example.iam2.service.impl.JWTServiceImpl;
import com.nimbusds.jose.JOSEException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Objects;

@Component
@Slf4j
public class JWTDecoderConfig implements JwtDecoder {

    @Autowired
    private JWTServiceImpl jwtService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            if(!jwtService.verifyToken(token)){
                throw new RuntimeException("Invalid token");
            }
            if(Objects.isNull(nimbusJwtDecoder)){
                SecretKey secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HS256");
                nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                        .macAlgorithm(MacAlgorithm.HS256)
                        .build();
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return nimbusJwtDecoder.decode(token);
    }
}

