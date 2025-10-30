package com.example.iam2.service.impl;

import com.example.iam2.entity.RoleEntity;
import com.example.iam2.entity.UserEntity;
import com.example.iam2.model.dto.UserDTO;
import com.example.iam2.model.response.LoginResponse;
import com.example.iam2.repository.RoleRepository;
import com.example.iam2.repository.UserRepository;
import com.example.iam2.service.KeycloakService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class KeycloakServiceImpl implements KeycloakService {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String userClientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret:}")
    private String userClientSecret;


    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDTO register(UserDTO userDTO) {
        //Lấy access token admin
        String adminToken = getAdminAccessToken();

        //Gọi API tạo user của keycloak
        String url = serverUrl + "/admin/realms/" + realm + "/users";

        Map<String, Object> body = new HashMap<>();
        body.put("username", userDTO.getUsername());
        body.put("email", userDTO.getEmail());
        body.put("enabled", true);
        body.put("firstName", userDTO.getFirstName());
        body.put("lastName", userDTO.getLastName());

        // Password
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", userDTO.getPassword());
        credentials.put("temporary", false);
        body.put("credentials", Collections.singletonList(credentials));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            UserEntity userEntity = modelMapper.map(userDTO,UserEntity.class);
            RoleEntity defaultRole= roleRepository.findByCode("ROLE_USER");
            userEntity.setRoles(Collections.singletonList(defaultRole));
            userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            userEntity.setCreatedBy("keycloak");
            userEntity.setLocked(false);
            userEntity.setDeleted(false);

            userRepository.save(userEntity);
            return userDTO;
        }

        throw new RuntimeException("Failed to register user in Keycloak: " + response.getBody());
    }

    @Override
    public void logout(String refreshToken) {
        String url = serverUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", userClientId);
        if (!userClientSecret.isEmpty()) {
            body.add("client_secret", userClientSecret);
        }
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Logout thành công trên Keycloak");
        } else {
            System.out.println("Logout thất bại: " + response.getBody());
        }
    }

    @Override
    public LoginResponse refreshAccessToken(String refreshToken) {
        String tokenUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false; // tự xử lý
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                // Không làm gì
            }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", userClientId);
        body.add("client_secret", userClientSecret);
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                entity,
                String.class
        );

        try {
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("Keycloak refresh lỗi: {}", response.getBody());
                return LoginResponse.builder()
                        .accessToken(null)
                        .refreshToken(null)
                        .build();
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.getBody());

            return LoginResponse.builder()
                    .accessToken(jsonNode.path("access_token").asText(null))
                    .refreshToken(jsonNode.path("refresh_token").asText(null))
                    .build();

        } catch (Exception e) {
            log.error("Làm mới token thất bại", e);
            return LoginResponse.builder()
                    .accessToken(null)
                    .refreshToken(null)
                    .build();
        }
    }



    private String getAdminAccessToken() {
        String url = serverUrl + "/realms/master/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", clientId);
        map.add("username", adminUsername);
        map.add("password", adminPassword);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        Map<String, Object> tokenResponse = restTemplate.postForObject(url, request, Map.class);

        if(tokenResponse != null && tokenResponse.containsKey("access_token")) {
            return tokenResponse.get("access_token").toString();
        }
        throw new RuntimeException("Cannot get admin access token from Keycloak");
    }
}
