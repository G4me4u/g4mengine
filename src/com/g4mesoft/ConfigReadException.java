package com.g4mesoft;

public class ConfigReadException extends RuntimeException {
	private static final long serialVersionUID = 5528011880172215309L;

    public ConfigReadException() {
        super();
    }

    public ConfigReadException(String message) {
        super(message);
    }

    public ConfigReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigReadException(Throwable cause) {
        super(cause);
    }
}
