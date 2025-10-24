package com.example.iam2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Email đã tồn tại
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<Object> handleDuplicate(DuplicateException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    //Token không hợp lệ
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String,Object>> handleInvalidToken(InvalidTokenException ex){
        Map<String,Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Invalid Token");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }
    //UserId trong token khác userId trong body
    @ExceptionHandler(InvalidUserID.class)
    public ResponseEntity<Map<String,Object>> handleInvalidUserID(InvalidUserID ex){
        Map<String,Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Invalid User ID");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    //Không tìm thấy người dùng
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(NotFoundException ex) {
        Map<String,Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "User Not Found");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
