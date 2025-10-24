package com.example.iam2.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class PermissionDTO {
    private Long id;
    private String name;
    private String description;
    private boolean deleted;
    List<RoleDTO> roleDTOList;
}
