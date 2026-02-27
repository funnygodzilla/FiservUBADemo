package com.fiserv.uba.user.exception;

import org.springframework.http.HttpStatus;

public class UserServiceException extends RuntimeException {

    private final HttpStatus status;

    public UserServiceException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
