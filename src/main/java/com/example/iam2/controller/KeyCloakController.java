package com.example.iam2.controller;

import com.example.iam2.model.dto.UserDTO;
import com.example.iam2.model.response.LoginResponse;
import com.example.iam2.service.KeycloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/keycloak")
public class KeyCloakController {

    @Autowired
    private KeycloakService keycloakService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        try {
            UserDTO createdUser = keycloakService.register(userDTO);
            return ResponseEntity.ok(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body("Đăng ký thất bại : " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Refresh-Token") String refreshToken) {
        try {
            keycloakService.logout(refreshToken);
            return ResponseEntity.ok("Logout thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Logout thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        LoginResponse response = keycloakService.refreshAccessToken(refreshToken);

        if (response.getAccessToken() == null) {
            // Token không hợp lệ
            Map<String, Object> error = new HashMap<>();
            error.put("error", "invalid_refresh_token");
            error.put("message", "Refresh token không hợp lệ hoặc session đã hết hạn");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        return ResponseEntity.ok(response);
    }

}
