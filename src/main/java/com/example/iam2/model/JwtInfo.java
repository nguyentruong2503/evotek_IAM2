package com.example.iam2.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class JwtInfo {
    private String jwtID;
    private Date issueTime;
    private Date expiredTime;
}
