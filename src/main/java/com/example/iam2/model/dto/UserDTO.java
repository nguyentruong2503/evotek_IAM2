package com.example.iam2.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Date birthday;
    private Boolean locked;
    private Boolean deleted;
    List<RoleDTO> roleDTOList;
}
