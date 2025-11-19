package com.moviescope.exception;

import com.moviescope.dto.response.ApiResponse;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Fixed: Proper generic type
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<String> handleException(Exception e) {
        return new ApiResponse<>("500", "Internal server error: " + e.getMessage(), null);
    }

    // Fixed: Proper generic type
    @ExceptionHandler(org.springframework.web.client.RestClientException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ApiResponse<String> handleRestClientException(org.springframework.web.client.RestClientException e) {
        return new ApiResponse<>("503", "TMDB service unavailable", null);
    }

    // Fixed: Proper generic type for validation errors
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Map<String, String>> handleValidationExceptions(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return new ApiResponse<>("400", "Validation failed", errors);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleRuntimeException(RuntimeException e) {
        return new ApiResponse<>("400", e.getMessage(), null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<String> handleAccessDeniedException(AccessDeniedException e) {
        return new ApiResponse<>("403", "Access denied", null);
    }

}