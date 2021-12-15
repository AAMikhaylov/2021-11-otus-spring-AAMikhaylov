package ru.otus.hw03.exceptions;

public class QuestionDaoException extends Exception {
    public QuestionDaoException(String message) {
        super(message);
    }

    public QuestionDaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
