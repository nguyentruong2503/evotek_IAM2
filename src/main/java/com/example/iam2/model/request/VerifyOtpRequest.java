package com.example.iam2.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpRequest {
    private String otp;
    private String newPassword;
}
