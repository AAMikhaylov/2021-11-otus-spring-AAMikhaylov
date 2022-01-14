package ru.otus.hw04.exceptions;

public class QuestionDaoException extends Exception {
    public QuestionDaoException(String message) {
        super(message);
    }

    public QuestionDaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
