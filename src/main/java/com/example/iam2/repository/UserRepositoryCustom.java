package com.example.iam2.repository;

import com.example.iam2.entity.UserEntity;

import java.util.List;

public interface UserRepositoryCustom {
    List<UserEntity> getAll(int page, int size);
    long countAll();
}
