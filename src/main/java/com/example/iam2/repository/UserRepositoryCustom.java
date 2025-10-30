package com.example.iam2.repository;

import com.example.iam2.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryCustom {
    List<UserEntity> getAll(int page, int size);
    long countAll();

    Optional<UserEntity> findByUsernameWithRolesAndPermissions(String username);
}
