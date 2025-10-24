package com.example.iam2.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("RedisHas")
@Builder
public class RedisToken {

    @Id
    private String jwtID;

    @TimeToLive()
    private Long expriredTime;
}
