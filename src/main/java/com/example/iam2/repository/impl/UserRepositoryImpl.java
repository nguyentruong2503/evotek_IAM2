package com.example.iam2.repository.impl;

import com.example.iam2.entity.UserEntity;
import com.example.iam2.repository.UserRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<UserEntity> getAll(int page, int size) {
        String sql = "SELECT * FROM users"; // lấy tất cả, không lọc locked/delete
        Query query = entityManager.createNativeQuery(sql, UserEntity.class);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM users";
        Query query = entityManager.createNativeQuery(sql);
        return ((Number) query.getSingleResult()).longValue();
    }

}
