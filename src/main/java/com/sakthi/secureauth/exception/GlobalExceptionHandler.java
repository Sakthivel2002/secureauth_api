package com.sakthi.secureauth.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildError(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI()
        );
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.FORBIDDEN,
                "Access denied",
                request.getRequestURI()
        );
    }


    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiError> handleInvalidRefreshToken(
            InvalidRefreshTokenException ex,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request.getRequestURI()
        );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong",
                request.getRequestURI()
        );
    }


    private ResponseEntity<ApiError> buildError(
            HttpStatus status,
            String message,
            String path
    ) {
        ApiError error = ApiError.builder()
                .status(status.value())
                .error(status.name())
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(error);
    }
}
