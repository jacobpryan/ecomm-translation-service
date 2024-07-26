package com.nm.translation.exception;

public class SqsServiceException extends Exception {
    public SqsServiceException(String message) {
        super(message);
    }
    public SqsServiceException(String message, Throwable root) {
        super(message, root);
    }
}

