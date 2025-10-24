package com.example.iam2.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignPermissionDTO {
    private Long roleID;
    private List<Long> permissionIds;
}
