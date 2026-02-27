package com.fiserv.uba.transaction.exception;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<Map<String,String>> handle(TransactionException ex){
        return ResponseEntity.status(ex.getStatus()).body(Map.of("error", ex.getMessage()));
    }
}
