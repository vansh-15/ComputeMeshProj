package com.computemesh.coordinator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String,Object>> handleResponseStatusException(ResponseStatusException ex){
        Map<String,Object> body = Map.of(
                "status", ex.getStatusCode().value(),
                "error", HttpStatus.valueOf(
                        ex.getStatusCode().value()).getReasonPhrase(),
                "message", ex.getReason()
        );

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(body);
    }

}
