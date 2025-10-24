package com.example.iam2.service;

import com.example.iam2.model.dto.AssignRoleDTO;
import com.example.iam2.model.dto.UserDTO;
import com.example.iam2.model.response.PagedResponse;
import com.example.iam2.model.response.UserDetail;
import com.example.iam2.model.response.UserProfile;

import java.util.List;

public interface UserService {
    UserDTO create (UserDTO userDTO);
    UserProfile findUserById(String token);

    void lockUser(Long id);

    void unlockUser(Long id);

    void deleteUser(Long id);

    void resetPassword(Long id);

    PagedResponse<UserDTO> getAllUsers(int page, int size);

    UserDetail userDetail(Long id);

    void assignRoleToUser(AssignRoleDTO assignRoleDTO);
}
