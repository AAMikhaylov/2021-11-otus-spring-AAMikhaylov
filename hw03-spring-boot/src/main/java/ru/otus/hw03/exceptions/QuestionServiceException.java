package ru.otus.hw03.exceptions;

public class QuestionServiceException extends Exception {
    public QuestionServiceException(String message) {
        super(message);
    }

    public QuestionServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}