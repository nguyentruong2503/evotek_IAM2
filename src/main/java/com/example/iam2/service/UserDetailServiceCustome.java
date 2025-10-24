package com.example.iam2.service;

import com.example.iam2.converter.UserConverter;
import com.example.iam2.entity.PermissionEntity;
import com.example.iam2.entity.RoleEntity;
import com.example.iam2.entity.UserEntity;
import com.example.iam2.model.dto.UserDTO;
import com.example.iam2.repository.UserRepository;
import com.example.iam2.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserDetailServiceCustome implements UserDetailsService {

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity = userRepository.findByUsernameAndLockedAndDeleted(username, false, false)
                .orElseThrow(() -> new UsernameNotFoundException("User không tồn tại hoặc đã bị khóa"));

        // Lọc role chưa bị xóa mềm
        Set<RoleEntity> activeRoles = entity.getRoles().stream()
                .filter(role -> !role.isDeleted())  // boolean deleted => dùng isDeleted()
                .collect(Collectors.toSet());

        // Lấy tất cả quyền từ roles hợp lệ
        Set<GrantedAuthority> authorities = new HashSet<>();

        for (RoleEntity role : activeRoles) {
            authorities.add(new SimpleGrantedAuthority(role.getCode()));

            // Lọc permission chưa bị xóa mềm
            Set<PermissionEntity> activePermissions = role.getPermissions().stream()
                    .filter(permission -> !permission.isDeleted()) // boolean deleted
                    .collect(Collectors.toSet());

            activePermissions.forEach(permission ->
                    authorities.add(new SimpleGrantedAuthority(permission.getName()))
            );
        }

        UserDTO dto = userConverter.toUserDTO(entity);
        CustomUserDetails customUserDetails = new CustomUserDetails(dto);
        customUserDetails.setAuthorities(authorities);

        // Log kiểm tra quyền thực tế
        log.info("User '{}' có quyền: {}", username,
                authorities.stream().map(GrantedAuthority::getAuthority).toList());

        return customUserDetails;
    }

}
