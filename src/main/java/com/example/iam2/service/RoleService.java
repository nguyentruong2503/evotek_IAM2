package com.example.iam2.service;


import com.example.iam2.model.dto.RoleDTO;
import com.example.iam2.model.response.PagedResponse;

public interface RoleService {
    RoleDTO createRole(RoleDTO roleDTO);

    RoleDTO updateRole(Long id,RoleDTO roleDTO);

    void deleteRole(Long id);

    PagedResponse<RoleDTO> getAll(int page, int size);
}
