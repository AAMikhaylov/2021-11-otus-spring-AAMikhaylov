package ru.otus.hw16.exceptions;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.core.RepositoryConstraintViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

@RequiredArgsConstructor
@RestControllerAdvice
public class AppExceptionHandler {
    private final ServerProperties serverProperties;
    private final static Map<String, String> UI_ERROR_MSG = Map.of(
            "delete from authors", "Нельзя удалить автора",
            "delete from genres", "Нельзя удалить жанр",
            "update books", "Нельзя обновить книгу",
            "insert into books", "Нельзя добавить книгу"
    );
    private final static Map<String, String> UI_ERROR_CAUSE = Map.of(
            "NULL not allowed for column \"AUTHOR_ID", "Автор не найден",
            "Referential integrity constraint violation: \"FK_BOOK_AUTHOR", "У автора есть книги",
            "Referential integrity constraint violation: \"FK_GENRE:", "У жанра есть книги"
    );

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(RuntimeException ex, WebRequest webRequest) {
        val responseStatus = HttpStatus.NOT_FOUND;
        val errorItems = getDefaultErrorItems(ex, webRequest, responseStatus);
        if (!serverProperties.getError().isIncludeException())
            errorItems.put("message", "No message available");
        return ResponseEntity
                .status(responseStatus)
                .body(errorItems);
    }

    @ExceptionHandler(RepositoryConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleBindingException(RepositoryConstraintViolationException ex, WebRequest webRequest) {
        val responseStatus = HttpStatus.BAD_REQUEST;
        val errorItems = getDefaultErrorItems(ex, webRequest, responseStatus);
        errorItems.put("errors",ex.getErrors().getAllErrors());
        return ResponseEntity
                .status(responseStatus)
                .body(errorItems);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(RuntimeException ex, WebRequest webRequest) {
        val responseStatus = HttpStatus.CONFLICT;
        val errorItems = getDefaultErrorItems(ex, webRequest, responseStatus);
        errorItems.put("message", getUIErrorMessage(getRootCause(ex).getMessage()));
        return ResponseEntity
                .status(responseStatus)
                .body(errorItems);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleDefaultException(RuntimeException ex, WebRequest webRequest) {
        val responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        val errorItems = getDefaultErrorItems(ex, webRequest, responseStatus);
        return ResponseEntity
                .status(responseStatus)
                .body(errorItems);
    }


    private Map<String, Object> getDefaultErrorItems(RuntimeException ex, WebRequest webRequest, HttpStatus responseStatus) {
        val errorItems = new LinkedHashMap<String, Object>();
        errorItems.put("timestamp", new Date());
        errorItems.put("status", responseStatus.value());
        errorItems.put("error", responseStatus.getReasonPhrase());
        if (serverProperties.getError().isIncludeException()) {
            errorItems.put("exception", ex.getClass().getName());
            try (val sw = new StringWriter();
                 val pw = new PrintWriter(sw)) {
                ex.printStackTrace(pw);
                errorItems.put("trace", sw.toString());
            } catch (Exception e) {
                errorItems.put("trace", "Error while printing stacktrace: " + e.getMessage());
            }
        }
        errorItems.put("message", ex.getMessage());
        errorItems.put("path", ((ServletWebRequest) webRequest).getRequest().getRequestURI());
        return errorItems;
    }

    private String getUIErrorMessage(String exceptionMessage) {
        var uiErrorMsg = "";
        for (String errorKey : UI_ERROR_MSG.keySet()) {
            if (exceptionMessage.toLowerCase().contains(errorKey.toLowerCase())) {
                uiErrorMsg = UI_ERROR_MSG.get(errorKey);
                break;
            }
        }
        if (uiErrorMsg.isEmpty())
            return exceptionMessage;
        var uiErrorCause = "";
        for (String causeKey : UI_ERROR_CAUSE.keySet()) {
            if (exceptionMessage.toLowerCase().contains(causeKey.toLowerCase())) {
                uiErrorCause = UI_ERROR_CAUSE.get(causeKey);
                break;
            }
        }
        if (uiErrorCause.isEmpty())
            return exceptionMessage;
        return String.format("%s. %s.", uiErrorMsg, uiErrorCause);
    }

    private Throwable getRootCause(Throwable throwable) {
        Objects.requireNonNull(throwable);
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }


}
