package com.example.iam2.service.impl;


import com.example.iam2.exception.InvalidTokenException;
import com.example.iam2.model.JwtInfo;
import com.example.iam2.model.RedisToken;
import com.example.iam2.model.TokenPayload;
import com.example.iam2.repository.RedisTokenRepository;
import com.example.iam2.security.CustomUserDetails;
import com.example.iam2.service.JWTService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.*;


@Service
public class JWTServiceImpl implements JWTService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Autowired
    private RedisTokenRepository redisTokenRepository;

    @Override
    public TokenPayload generateAccessToken(CustomUserDetails userDetails){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        Date issueTime = new Date();
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(1800); //30 phút = 30*60 = 1800s
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

        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header,payload);
        try {
            jwsObject.sign(new MACSigner(secretKey));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        String token = jwsObject.serialize();

        return TokenPayload.builder()
                .token(token)
                .jwtID(jwtID)
                .expiredTime(expirationDate)
                .build();
    }

    @Override
    public TokenPayload generateRefreshToken(CustomUserDetails userDetails){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        Date issueTime = new Date();
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(2592000); //30 ngày = 30*24*60*60 = 2592000s
        Date expirationDate = Date.from(expiry);
        String jwtID = UUID.randomUUID().toString();


        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userDetails.getUsername())
                .issueTime(issueTime)
                .expirationTime(expirationDate)
                .jwtID(jwtID)
                .claim("type", "refresh")
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header,payload);
        try {
            jwsObject.sign(new MACSigner(secretKey));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        String token =  jwsObject.serialize();

        return TokenPayload.builder()
                .token(token)
                .jwtID(jwtID)
                .expiredTime(expirationDate)
                .build();
    }

    @Override
    public boolean verifyToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if(expirationTime.before(new Date())){
            throw new InvalidTokenException("Token đã hết hạn");
        }

        String jwtID = signedJWT.getJWTClaimsSet().getJWTID();
        Optional<RedisToken> redisToken = redisTokenRepository.findById(jwtID);
        if(redisToken.isPresent()){
            throw new InvalidTokenException("Token không hợp lệ");
        }
         return signedJWT.verify(new MACVerifier(secretKey));
    }

    public JwtInfo parseToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        String jwtID = signedJWT.getJWTClaimsSet().getJWTID();
        Date issueTine = signedJWT.getJWTClaimsSet().getIssueTime();
        Date expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        return JwtInfo.builder()
                .jwtID(jwtID)
                .issueTime(issueTine)
                .expiredTime(expiredTime)
                .build();
    }

    @Override
    public boolean checkRefreshToken(String refreshToken) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(refreshToken);

        boolean isValidSignature = signedJWT.verify(new MACVerifier(secretKey));
        if (!isValidSignature) {
            throw new RuntimeException("Chữ ký không hợp lệ");
        }

        Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (exp.before(new Date())) {
            throw new RuntimeException("Refresh token hết hạn");
        }

        String type = (String) signedJWT.getJWTClaimsSet().getClaim("type");
        if (!"refresh".equals(type)) {
            throw new RuntimeException("Không phải refresh token");
        }

        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        if (redisTokenRepository.findById(jwtId).isEmpty()) {
            throw new RuntimeException("Refresh token không tồn tại hoặc đã bị thu hồi");
        }
        return true;
    }

    public String getSubject(String token) throws ParseException {
        return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
    }

    public Long getUserIdFromToken(String token) throws ParseException {
        return SignedJWT.parse(token).getJWTClaimsSet().getLongClaim("userId");
    }

}
