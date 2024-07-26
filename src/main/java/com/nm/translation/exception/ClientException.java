package com.nm.translation.exception;

public class ClientException extends Exception {
	public ClientException(String message) {
		super(message);
	}

	public ClientException(String message, Throwable error) {
		super(message, error);
	}
}
