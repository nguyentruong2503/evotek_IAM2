package com.example.iam2.service;

import com.example.iam2.model.TokenPayload;
import com.example.iam2.security.CustomUserDetails;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface JWTService {
    TokenPayload generateAccessToken(CustomUserDetails userDetails);
    TokenPayload generateRefreshToken(CustomUserDetails userDetails);
    boolean verifyToken(String token) throws ParseException, JOSEException;
    boolean checkRefreshToken(String refreshToken) throws ParseException, JOSEException;
}
