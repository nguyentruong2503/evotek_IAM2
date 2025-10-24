package com.example.iam2.repository.impl;

import com.example.iam2.entity.PermissionEntity;
import com.example.iam2.repository.PermissionRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PermissionRepositoryImpl implements PermissionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PermissionEntity> getAll(int page, int size) {
        String sql = "SELECT * FROM permissions";
        Query query = entityManager.createNativeQuery(sql, PermissionEntity.class);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM permissions";
        Query query = entityManager.createNativeQuery(sql);
        return ((Number) query.getSingleResult()).longValue();
    }
}
