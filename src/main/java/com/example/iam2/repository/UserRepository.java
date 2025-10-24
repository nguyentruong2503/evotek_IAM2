package com.example.iam2.repository;

import com.example.iam2.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long>, UserRepositoryCustom {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phoneNumber);

    Optional<UserEntity> findByUsernameAndLockedAndDeleted(String username, boolean locked, boolean deleted);
}
