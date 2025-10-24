package com.example.iam2.service.impl;

import com.example.iam2.entity.PermissionEntity;
import com.example.iam2.entity.RoleEntity;
import com.example.iam2.exception.DuplicateException;
import com.example.iam2.exception.NotFoundException;
import com.example.iam2.model.dto.AssignPermissionDTO;
import com.example.iam2.model.dto.PermissionDTO;
import com.example.iam2.model.dto.RoleDTO;
import com.example.iam2.model.dto.UserDTO;
import com.example.iam2.model.response.PagedResponse;
import com.example.iam2.repository.PermissionRepository;
import com.example.iam2.repository.RoleRepository;
import com.example.iam2.service.PermissionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissonServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PermissionDTO createPermission(PermissionDTO permissionDTO) {
       if( permissionRepository.existsByName(permissionDTO.getName())){
           throw new DuplicateException("Quyền đã tồn tại");
       }
       PermissionEntity permissionEntity = modelMapper.map(permissionDTO,PermissionEntity.class);
       permissionRepository.save(permissionEntity);
       return permissionDTO;
    }

    @Override
    public PermissionDTO updatePermission(Long id, PermissionDTO permissionDTO) {
        PermissionEntity permissionEntity = permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy quyền"));

        permissionEntity.setDescription(permissionDTO.getDescription());
        permissionRepository.save(permissionEntity);

        return permissionDTO;
    }

    @Override
    public void deletePermission(Long id) {
        PermissionEntity permissionEntity = permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy quyền"));

        permissionEntity.setDeleted(true);
        permissionRepository.save(permissionEntity);
    }

    @Override
    public PagedResponse<PermissionDTO> getAll(int page, int size) {
        List<PermissionEntity> permissionEntityList = permissionRepository.getAll(page, size);
        long totalItems = permissionRepository.countAll();
        List<PermissionDTO> permissionDTOList = new ArrayList<>();
        for(PermissionEntity permissionEntity : permissionEntityList){
            PermissionDTO permissionDTO = modelMapper.map(permissionEntity, PermissionDTO.class);
            List<RoleDTO> roleDTOs = permissionEntity.getRoles().stream()
                    .map(role -> modelMapper.map(role, RoleDTO.class))
                    .collect(Collectors.toList());
            permissionDTO.setRoleDTOList(roleDTOs);

            permissionDTOList.add(permissionDTO);
        }
        return new PagedResponse<>(permissionDTOList, page, size, totalItems);
    }

    @Override
    public void assignPermissionToRole(AssignPermissionDTO assignPermissionDTO) {
        RoleEntity roleEntity = roleRepository.findById(assignPermissionDTO.getRoleID())
                .orElseThrow(() -> new NotFoundException("Role không tồn tại"));

        //Chỉ add thêm quyền mới vào, không xóa quyền cũ đi
        List<PermissionEntity> newPermissions = permissionRepository.findByIdIn(assignPermissionDTO.getPermissionIds());
        roleEntity.getPermissions().addAll(newPermissions);
        roleRepository.save(roleEntity);
    }
}
