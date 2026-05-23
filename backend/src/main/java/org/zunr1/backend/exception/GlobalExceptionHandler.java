package org.zunr1.backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.zunr1.backend.dto.Result;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Result<Void>> handleNotFound(NotFoundException e){
        return ResponseEntity.status(404).body(Result.error(400, e.getMessage()));
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Result<Void>> handleBadRequest(BadRequestException e) {
        return ResponseEntity.badRequest().body(Result.error(400, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        e.printStackTrace(); // 记录日志
        return ResponseEntity.status(500).body(Result.error(500, "系统内部错误"));
    }

    @ExceptionHandler(TokenErrorException.class)
    public ResponseEntity<Result<Void>> handleTokenError(TokenErrorException e) {
        return ResponseEntity.status(401).body(Result.error(401, e.getMessage()));
    }
}
