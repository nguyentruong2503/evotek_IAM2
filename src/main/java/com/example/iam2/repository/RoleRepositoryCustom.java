package com.example.iam2.repository;

import com.example.iam2.entity.RoleEntity;

import java.util.List;

public interface RoleRepositoryCustom {
    List<RoleEntity> getAll(int page, int size);
    long countAll();
}
