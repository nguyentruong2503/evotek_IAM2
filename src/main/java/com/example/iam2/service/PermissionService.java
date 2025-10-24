package com.example.iam2.service;

import com.example.iam2.model.dto.AssignPermissionDTO;
import com.example.iam2.model.dto.AssignRoleDTO;
import com.example.iam2.model.dto.PermissionDTO;
import com.example.iam2.model.response.PagedResponse;

import java.util.List;

public interface PermissionService {
    PermissionDTO createPermission(PermissionDTO permissionDTO);

    PermissionDTO updatePermission(Long id,PermissionDTO permissionDTO);

    void deletePermission(Long id);

    PagedResponse<PermissionDTO> getAll(int page, int size);

    void assignPermissionToRole(AssignPermissionDTO assignPermissionDTO);
}
