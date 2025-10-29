package com.example.iam2.service.impl;

import com.example.iam2.converter.UserConverter;
import com.example.iam2.entity.RoleEntity;
import com.example.iam2.entity.UserEntity;
import com.example.iam2.exception.DuplicateException;
import com.example.iam2.exception.InvalidTokenException;
import com.example.iam2.exception.NotFoundException;
import com.example.iam2.model.dto.AssignRoleDTO;
import com.example.iam2.model.dto.RoleDTO;
import com.example.iam2.model.dto.UserDTO;
import com.example.iam2.model.response.PagedResponse;
import com.example.iam2.model.response.UserDetail;
import com.example.iam2.model.response.UserProfile;
import com.example.iam2.repository.RoleRepository;
import com.example.iam2.repository.UserRepository;
import com.example.iam2.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JWTServiceImpl jwtService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private JwtDecoder keycloakJwtDecoder;

    @Value("${iam.security.keycloak-enabled:false}")
    private boolean keycloakEnabled;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDTO create(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new DuplicateException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateException("Email đã tồn tại");
        }
        if (userRepository.existsByPhone(userDTO.getPhone())) {
            throw new DuplicateException("Số điện đã tồn tại");
        }

        UserEntity userEntity = modelMapper.map(userDTO,UserEntity.class);
        RoleEntity defaultRole= roleRepository.findByCode("ROLE_USER");
        userEntity.setRoles(Collections.singletonList(defaultRole));
        userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userEntity.setLocked(false);
        userEntity.setDeleted(false);

        userRepository.save(userEntity);
        return userDTO;
    }

    public UserProfile findUserById(String token) {
        Long userIdFromToken;

        try {
            if (keycloakEnabled) {
                // decode token Keycloak, lấy preferred_username
                String username = keycloakJwtDecoder.decode(token).getClaimAsString("preferred_username");
                UserEntity userEntity = userRepository.findByUsernameAndLockedAndDeleted(username, false, false)
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
                return mapToProfile(userEntity);
            } else {
                // decode token nội bộ
                userIdFromToken = jwtService.getUserIdFromToken(token);
                UserEntity userEntity = userRepository.findById(userIdFromToken)
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
                return mapToProfile(userEntity);
            }
        } catch (ParseException e) {
            throw new InvalidTokenException("Token không hợp lệ");
        }
    }

    private UserProfile mapToProfile(UserEntity userEntity) {
        UserProfile profile = new UserProfile();
        profile.setEmail(userEntity.getEmail());
        profile.setFirstName(userEntity.getFirstName());
        profile.setLastName(userEntity.getLastName());
        profile.setPhone(userEntity.getPhone());
        profile.setBirthday(userEntity.getBirthday());
        return profile;
    }

    @Override
    public void lockUser(Long id) {
        UserEntity userEntity = userRepository.findById(id).get();
        userEntity.setLocked(true);
        userRepository.save(userEntity);
    }

    @Override
    public void unlockUser(Long id) {
        UserEntity userEntity = userRepository.findById(id).get();
        userEntity.setLocked(false);
        userRepository.save(userEntity);
    }

    @Override
    public void deleteUser(Long id) {
        UserEntity userEntity = userRepository.findById(id).get();
        userEntity.setDeleted(true);
        userRepository.save(userEntity);
    }

    @Override
    public void resetPassword(Long id) {
        UserEntity userEntity = userRepository.findById(id).get();
        userEntity.setPassword(passwordEncoder.encode("123456aA@"));
        userRepository.save(userEntity);
    }

    @Override
    public PagedResponse<UserDTO> getAllUsers(int page, int size) {
        List<UserEntity> entities = userRepository.getAll(page, size);
        long totalItems = userRepository.countAll();
        List<UserDTO> dtos = entities.stream()
                .map(userConverter::toUserDTO)
                .toList();

        return new PagedResponse<>(dtos, page, size, totalItems);
    }

    @Override
    public UserDetail userDetail(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User không tồn tại"));
        UserDetail userDetail = modelMapper.map(userEntity, UserDetail.class);
        List<RoleDTO> roleDTOs = userEntity.getRoles().stream()
                .map(role -> modelMapper.map(role, RoleDTO.class))
                .collect(Collectors.toList());
        userDetail.setRoles(roleDTOs);
        return userDetail;
    }

    @Override
    public void assignRoleToUser(AssignRoleDTO assignRoleDTO) {
        UserEntity userEntity= userRepository.findById(assignRoleDTO.getUserId())
                .orElseThrow(() -> new NotFoundException("User không tồn tại"));
        List<RoleEntity> newRoles = roleRepository.findByIdIn(assignRoleDTO.getRoleIds());
        userEntity.getRoles().addAll(newRoles);
        userRepository.save(userEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GrantedAuthority> getAuthoritiesByUsername(String username) {
        UserEntity user = userRepository.findByUsernameWithRolesAndPermissions(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Set<GrantedAuthority> authorities = new HashSet<>();
        for (RoleEntity role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getCode()));
            role.getPermissions().forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getName())));
        }
        return new ArrayList<>(authorities);
    }

}
