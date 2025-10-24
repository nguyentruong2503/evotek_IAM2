package com.example.iam2.exception;

public class InvalidUserID extends RuntimeException {
    public InvalidUserID(String message) {
        super(message);
    }
}
