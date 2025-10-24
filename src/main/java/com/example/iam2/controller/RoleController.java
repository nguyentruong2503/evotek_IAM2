package com.example.iam2.controller;

import com.example.iam2.model.dto.PermissionDTO;
import com.example.iam2.model.dto.RoleDTO;
import com.example.iam2.model.response.PagedResponse;
import com.example.iam2.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createRole(@RequestBody RoleDTO roleDTO) {
        roleService.createRole(roleDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "status", "success",
                        "message", "Thêm mới vai trò thành công!"
                ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePermisson(@PathVariable Long id,
                                             @RequestBody RoleDTO roleDTO) {
        roleService.updateRole(id,roleDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of(
                        "status", "success",
                        "message", "Sửa vai trò thành công!"
                ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        roleService.deleteRole(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PagedResponse<RoleDTO> getAll(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "5") int size) {
        return roleService.getAll(page,size);
    }

}
