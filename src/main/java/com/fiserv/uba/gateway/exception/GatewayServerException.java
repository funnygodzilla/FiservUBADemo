package com.fiserv.uba.gateway.exception;

public class GatewayServerException extends RuntimeException {

    public GatewayServerException(String message) {
        super(message);
    }

    public GatewayServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
