package com.example.iam2.controller;



import com.example.iam2.model.request.LoginRequest;
import com.example.iam2.model.response.LoginResponse;
import com.example.iam2.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@Slf4j
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/auth/login")
    LoginResponse login(@RequestBody LoginRequest loginRequest){
        return authenticationService.login(loginRequest);
    }

    @PostMapping("/auth/logout")
    void logout(@RequestHeader("Authorization") String authToken,
                @RequestHeader("Refresh-Token") String refreshTokenHeader) throws ParseException {
        String token = authToken.replace("Bearer ", "");
        authenticationService.logout(token,refreshTokenHeader);
    }

    @PostMapping("/auth/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        LoginResponse response = authenticationService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

}
