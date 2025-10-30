package com.example.iam2.service;

import com.example.iam2.model.dto.UserDTO;
import com.example.iam2.model.response.LoginResponse;

public interface KeycloakService {
    UserDTO register (UserDTO userDTO);

    void logout(String refreshToken);

    LoginResponse refreshAccessToken(String refreshToken);
}
