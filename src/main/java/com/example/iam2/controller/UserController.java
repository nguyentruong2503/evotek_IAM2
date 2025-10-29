package com.example.iam2.controller;

import com.example.iam2.model.dto.AssignRoleDTO;
import com.example.iam2.model.dto.UserDTO;
import com.example.iam2.model.response.PagedResponse;
import com.example.iam2.model.response.UserDetail;
import com.example.iam2.model.response.UserProfile;
import com.example.iam2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        userService.create(userDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "status", "success",
                        "message", "Thêm mới user thành công!"
                ));
    }

    @GetMapping("/me")
    public UserProfile getMyProfile(@RequestHeader("Authorization") String authToken) {
        String token = authToken.replace("Bearer ", "");
        return userService.findUserById(token);
    }

    @PostMapping("/{id}/lock")
    @PreAuthorize("hasAuthority('LOCK_USER')")
    public ResponseEntity<?> lockUser(@PathVariable Long id) {
        userService.lockUser(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of(
                        "status", "success",
                        "message", "Khóa thành công!"
                ));
    }

    @PostMapping("/{id}/unlock")
    @PreAuthorize("hasAuthority('UNLOCK_USER')")
    public ResponseEntity<?> unlockUser(@PathVariable Long id) {
        userService.unlockUser(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of(
                        "status", "success",
                        "message", "Mở khóa thành công!"
                ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_USER')")
    public ResponseEntity<?> softDeleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of(
                        "status", "success",
                        "message", "Xóa thành công!"
                ));
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> resetPassW(@PathVariable Long id) {
        userService.resetPassword(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of(
                        "status", "success",
                        "message", "Reset mật khẩu thành công!"
                ));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public PagedResponse<UserDTO> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        return userService.getAllUsers(page, size);
    }

    @GetMapping("/profile/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public UserDetail profile(@PathVariable Long id) {
        return userService.userDetail(id);
    }

    @PostMapping("/assignRole")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignRoleToUser(@RequestBody AssignRoleDTO assignRoleDTO) {
        userService.assignRoleToUser(assignRoleDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of(
                        "status", "success",
                        "message", "Gán vai trò thành công!"
                ));
    }


}
