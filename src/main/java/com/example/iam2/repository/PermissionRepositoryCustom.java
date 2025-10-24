package com.example.iam2.repository;

import com.example.iam2.entity.PermissionEntity;

import java.util.List;

public interface PermissionRepositoryCustom {
    List<PermissionEntity> getAll(int page, int size);
    long countAll();
}
