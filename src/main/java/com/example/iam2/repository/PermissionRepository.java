package com.example.iam2.repository;

import com.example.iam2.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<PermissionEntity,Long>, PermissionRepositoryCustom {
    Optional<PermissionEntity> findByName(String name);
    boolean existsByName(String name);

    List<PermissionEntity> findByIdIn(List<Long> ids);
}
