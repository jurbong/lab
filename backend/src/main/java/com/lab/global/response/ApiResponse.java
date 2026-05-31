package com.lab.global.response;
import lombok.*;
@Getter @AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    public static <T> ApiResponse<T> ok(String message, T data){return new ApiResponse<>(true,message,data);}
    public static ApiResponse<Void> ok(String message){return new ApiResponse<>(true,message,null);}
}
