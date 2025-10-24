package com.example.iam2.model.response;

import com.example.iam2.model.dto.RoleDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDetail {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private boolean locked;
    private boolean deleted;
    private List<RoleDTO> roles;
}
