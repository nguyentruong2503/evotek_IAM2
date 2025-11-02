package com.example.iam2.service.impl;

import com.example.iam2.exception.InvalidTokenException;
import com.example.iam2.model.JwtInfo;
import com.example.iam2.model.RedisToken;
import com.example.iam2.model.TokenPayload;
import com.example.iam2.repository.RedisTokenRepository;
import com.example.iam2.security.CustomUserDetails;
import com.example.iam2.service.JWTService;
import com.example.iam2.util.RsaKeyLoader;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.*;

@Service
public class JWTServiceImpl implements JWTService {

    @Value("${jwt.private-key}")
    private Resource privateKeyResource;

    @Value("${jwt.public-key}")
    private Resource publicKeyResource;

    @Autowired
    private RsaKeyLoader rsaKeyLoader;

    @Autowired
    private RedisTokenRepository redisTokenRepository;

    @Override
    public TokenPayload generateAccessToken(CustomUserDetails userDetails) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.RS256);

            Date issueTime = new Date();
            Instant now = Instant.now();
            Instant expiry = now.plusSeconds(1800); // 30 minutes
            Date expirationDate = Date.from(expiry);
            String jwtID = UUID.randomUUID().toString();

            List<String> roles = new ArrayList<>();
            List<String> permissions = new ArrayList<>();
            userDetails.getAuthorities().forEach(auth -> {
                String authority = auth.getAuthority();
                if (authority.startsWith("ROLE_")) {
                    roles.add(authority.substring("ROLE_".length()));
                } else {
                    permissions.add(authority);
                }
            });

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userDetails.getUsername())
                    .claim("userId", userDetails.getUserID())
                    .claim("roles", roles)
                    .claim("permissions", permissions)
                    .issueTime(issueTime)
                    .expirationTime(expirationDate)
                    .jwtID(jwtID)
                    .claim("type", "access")
                    .build();

            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            var privateKey = rsaKeyLoader.loadPrivateKey(privateKeyResource);
            signedJWT.sign(new RSASSASigner(privateKey));

            return TokenPayload.builder()
                    .token(signedJWT.serialize())
                    .jwtID(jwtID)
                    .expiredTime(expirationDate)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Cannot generate access token", e);
        }
    }

    @Override
    public TokenPayload generateRefreshToken(CustomUserDetails userDetails) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.RS256);

            Date issueTime = new Date();
            Instant now = Instant.now();
            Instant expiry = now.plusSeconds(2592000); // 30 days
            Date expirationDate = Date.from(expiry);
            String jwtID = UUID.randomUUID().toString();

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userDetails.getUsername())
                    .issueTime(issueTime)
                    .expirationTime(expirationDate)
                    .jwtID(jwtID)
                    .claim("type", "refresh")
                    .build();

            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            var privateKey = rsaKeyLoader.loadPrivateKey(privateKeyResource);
            signedJWT.sign(new RSASSASigner(privateKey));

            return TokenPayload.builder()
                    .token(signedJWT.serialize())
                    .jwtID(jwtID)
                    .expiredTime(expirationDate)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Cannot generate refresh token", e);
        }
    }

    @Override
    public boolean verifyToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expirationTime.before(new Date())) {
            throw new InvalidTokenException("Token đã hết hạn");
        }

        var publicKey = rsaKeyLoader.loadPublicKey(publicKeyResource);
        boolean validSignature = signedJWT.verify(new RSASSAVerifier(publicKey));

        if (!validSignature) {
            throw new InvalidTokenException("Chữ ký không hợp lệ");
        }

        String jwtID = signedJWT.getJWTClaimsSet().getJWTID();
        if (redisTokenRepository.findById(jwtID).isPresent()) {
            throw new InvalidTokenException("Token không hợp lệ hoặc đã bị thu hồi");
        }

        return true;
    }

    @Override
    public boolean checkRefreshToken(String refreshToken) throws ParseException, JOSEException {
        try {
            SignedJWT signedJWT = SignedJWT.parse(refreshToken);

            var publicKey = rsaKeyLoader.loadPublicKey(publicKeyResource);
            boolean isValidSignature = signedJWT.verify(new RSASSAVerifier(publicKey));

            if (!isValidSignature) {
                throw new InvalidTokenException("Chữ ký không hợp lệ");
            }

            Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (exp.before(new Date())) {
                throw new InvalidTokenException("Refresh token đã hết hạn");
            }

            String type = (String) signedJWT.getJWTClaimsSet().getClaim("type");
            if (!"refresh".equals(type)) {
                throw new InvalidTokenException("Không phải refresh token");
            }

            String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
            if (redisTokenRepository.findById(jwtId).isEmpty()) {
                throw new InvalidTokenException("Refresh token không tồn tại hoặc đã bị thu hồi");
            }

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Không thể xác minh refresh token", e);
        }
    }

    public JwtInfo parseToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return JwtInfo.builder()
                .jwtID(signedJWT.getJWTClaimsSet().getJWTID())
                .issueTime(signedJWT.getJWTClaimsSet().getIssueTime())
                .expiredTime(signedJWT.getJWTClaimsSet().getExpirationTime())
                .build();
    }

    public String getSubject(String token) throws ParseException {
        return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
    }

    public Long getUserIdFromToken(String token) throws ParseException {
        return SignedJWT.parse(token).getJWTClaimsSet().getLongClaim("userId");
    }
}
