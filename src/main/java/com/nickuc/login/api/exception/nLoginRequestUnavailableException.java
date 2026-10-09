package com.nickuc.login.api.exception;

public class nLoginRequestUnavailableException
extends IllegalStateException {
    public nLoginRequestUnavailableException(String message) {
        super(message);
    }

    public nLoginRequestUnavailableException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

