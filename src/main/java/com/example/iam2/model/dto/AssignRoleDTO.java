package com.example.iam2.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignRoleDTO {
    private Long userId;
    private List<Long> roleIds;
}
