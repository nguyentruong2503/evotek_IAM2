package com.example.iam2.service.impl;

import com.example.iam2.entity.PermissionEntity;
import com.example.iam2.entity.RoleEntity;
import com.example.iam2.exception.DuplicateException;
import com.example.iam2.exception.NotFoundException;
import com.example.iam2.model.dto.PermissionDTO;
import com.example.iam2.model.dto.RoleDTO;
import com.example.iam2.model.response.PagedResponse;
import com.example.iam2.repository.RoleRepository;
import com.example.iam2.service.RoleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {
        if(roleRepository.existsByCode(roleDTO.getCode())){
            throw new DuplicateException("Vai trò đã tồn tại");
        }

        String role = "ROLE_" + roleDTO.getCode() ;

        RoleEntity roleEntity = modelMapper.map(roleDTO,RoleEntity.class);
        roleEntity.setCode(role);
        roleRepository.save(roleEntity);

        return roleDTO;
    }

    @Override
    public RoleDTO updateRole(Long id, RoleDTO roleDTO) {
        RoleEntity roleEntity = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy vai trò"));

        roleEntity.setName(roleDTO.getName());
        roleRepository.save(roleEntity);

        return roleDTO;
    }

    @Override
    public void deleteRole(Long id) {
        RoleEntity roleEntity = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy vai trò"));

        roleEntity.setDeleted(true);
        roleRepository.save(roleEntity);
    }

    @Override
    public PagedResponse<RoleDTO> getAll(int page, int size) {
        List<RoleEntity> roleEntityList = roleRepository.getAll(page, size);
        long totalItems = roleRepository.countAll();
        List<RoleDTO> roleDTOList = new ArrayList<>();
        for(RoleEntity roleEntity : roleEntityList){
            RoleDTO roleDTO = modelMapper.map(roleEntity, RoleDTO.class);
            roleDTOList.add(roleDTO);
        }
        return new PagedResponse<>(roleDTOList, page, size, totalItems);
    }
}
