package com.example.iam2.service;

import com.example.iam2.model.request.LoginRequest;
import com.example.iam2.model.response.LoginResponse;

import java.text.ParseException;

public interface AuthenticationService {
    public LoginResponse login(LoginRequest request);

    public void logout(String accessToken, String refreshToken) throws ParseException;

    public LoginResponse refreshToken(String refreshToken);
}
