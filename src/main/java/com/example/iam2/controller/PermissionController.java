package com.example.iam2.controller;

import com.example.iam2.model.dto.AssignPermissionDTO;
import com.example.iam2.model.dto.AssignRoleDTO;
import com.example.iam2.model.dto.PermissionDTO;
import com.example.iam2.model.response.PagedResponse;
import com.example.iam2.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/permisson")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping
    @PreAuthorize("hasPermission(null,'CREATE_PERMISSION')")
    public ResponseEntity<?> createPermisson(@RequestBody PermissionDTO permissionDTO) {
        permissionService.createPermission(permissionDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "status", "success",
                        "message", "Thêm mới quyền thành công!"
                ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'UPDATE_PERMISSION')")
    public ResponseEntity<?> updatePermisson(@PathVariable Long id,
                                    @RequestBody PermissionDTO permissionDTO) {
        permissionService.updatePermission(id,permissionDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of(
                        "status", "success",
                        "message", "Sửa quyền thành công!"
                ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'DELETE_PERMISSION')")
    public void delete(@PathVariable Long id) {
        permissionService.deletePermission(id);
    }

    @GetMapping
    @PreAuthorize("hasPermission(null, 'VIEW_PERMISSION')")
    public PagedResponse<PermissionDTO> getAll(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "5") int size) {
        return permissionService.getAll(page,size);
    }

    @PostMapping("/assignPermission")
    @PreAuthorize("hasPermission(null, 'ASSIGN_PERMISSION')")
    public ResponseEntity<?> assignPermissionToRole(@RequestBody AssignPermissionDTO assignPermissionDTO) {
        permissionService.assignPermissionToRole(assignPermissionDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of(
                        "status", "success",
                        "message", "Gán quyền thành công!"
                ));
    }



}
