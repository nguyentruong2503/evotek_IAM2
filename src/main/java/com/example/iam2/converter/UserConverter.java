package com.example.iam2.converter;

import com.example.iam2.entity.UserEntity;
import com.example.iam2.model.dto.RoleDTO;
import com.example.iam2.model.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserConverter {

    @Autowired
    private ModelMapper modelMapper;

    public UserDTO toUserDTO(UserEntity userEntity){
        UserDTO userDTO = modelMapper.map(userEntity, UserDTO.class);
        List<RoleDTO> roleDTOs = userEntity.getRoles().stream()
                .map(role -> modelMapper.map(role, RoleDTO.class))
                .collect(Collectors.toList());
        userDTO.setRoleDTOList(roleDTOs);
        return userDTO;
    }

}
