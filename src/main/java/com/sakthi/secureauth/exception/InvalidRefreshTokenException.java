package com.sakthi.secureauth.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(String msg) {
        super(msg);
    }
}
