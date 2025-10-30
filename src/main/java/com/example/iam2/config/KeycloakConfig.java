package com.example.iam2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;

@Configuration
public class KeycloakConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String keycloakIssuerUri;

    @Bean
    public JwtDecoder keycloakJwtDecoder() {
        return JwtDecoders.fromIssuerLocation(keycloakIssuerUri);
    }
}
