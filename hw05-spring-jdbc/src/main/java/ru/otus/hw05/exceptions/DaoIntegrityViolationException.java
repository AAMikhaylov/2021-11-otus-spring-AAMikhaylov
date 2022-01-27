package ru.otus.hw05.exceptions;

public class DaoIntegrityViolationException extends DaoException {
    public DaoIntegrityViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DaoIntegrityViolationException(Throwable cause) {
        super(cause);
    }
}
