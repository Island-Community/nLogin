package com.nickuc.login.api.exception;

public class nLoginNotReadyException
extends IllegalStateException {
    public nLoginNotReadyException(String message) {
        super(message);
    }
}

