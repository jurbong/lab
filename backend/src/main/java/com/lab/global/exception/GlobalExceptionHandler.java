package com.lab.global.exception;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String,Object>> api(ApiException e){
        return ResponseEntity.status(e.getStatus()).body(Map.of("success",false,"message",e.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> valid(MethodArgumentNotValidException e){
        String msg=e.getBindingResult().getFieldErrors().stream().findFirst()
            .map(x->x.getField()+": "+x.getDefaultMessage()).orElse("입력값이 올바르지 않습니다.");
        return ResponseEntity.badRequest().body(Map.of("success",false,"message",msg));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> ex(Exception e){
        return ResponseEntity.internalServerError().body(Map.of("success",false,"message",e.getMessage()));
    }
}
