package com.example.iam2.repository;


import com.example.iam2.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<RoleEntity, Long>, RoleRepositoryCustom {
    RoleEntity findByCode(String code);

    boolean existsByCode(String code);
    List<RoleEntity> findByIdIn(List<Long> ids);
}
