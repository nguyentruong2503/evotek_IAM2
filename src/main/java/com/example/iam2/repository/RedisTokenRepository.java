package com.example.iam2.repository;

import com.example.iam2.model.RedisToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisTokenRepository extends CrudRepository<RedisToken, String> {
}
