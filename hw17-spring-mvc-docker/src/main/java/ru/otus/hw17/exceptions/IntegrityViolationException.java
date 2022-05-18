package ru.otus.hw17.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class IntegrityViolationException extends RuntimeException {
    public IntegrityViolationException() {
        super();
    }

    public IntegrityViolationException(String message) {
        super(message);
    }

    public IntegrityViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
