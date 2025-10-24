package com.example.iam2.repository.impl;

import com.example.iam2.entity.RoleEntity;
import com.example.iam2.repository.RoleRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleRepositoryImpl implements RoleRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<RoleEntity> getAll(int page, int size) {
        String sql = "SELECT * FROM roles";
        Query query = entityManager.createNativeQuery(sql, RoleEntity.class);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM roles";
        Query query = entityManager.createNativeQuery(sql);
        return ((Number) query.getSingleResult()).longValue();
    }
}
