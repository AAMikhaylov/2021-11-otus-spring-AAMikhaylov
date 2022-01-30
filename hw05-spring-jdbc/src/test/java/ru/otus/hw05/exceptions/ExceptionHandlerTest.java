package ru.otus.hw05.exceptions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.NoSuchMessageException;
import ru.otus.hw05.io.IOMessageService;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;


@SpringBootTest(classes = {ExceptionHandler.class})
@DisplayName("Класс ExceptionHandler должен:")
class ExceptionHandlerTest {

    @Autowired
    ExceptionHandler exceptionHandler;
    @MockBean
    private IOMessageService ioMessageService;

    private final static List<String> localizedMessages = new ArrayList<>();
    private final static List<String> simpleMessages = new ArrayList<>();

    private final static String KNOWN_EXCEPTION_MESSAGE = "known message id";
    private final static String UNKNOWN_EXCEPTION_MESSAGE = "any message";

    @BeforeEach
    void setUp() {
        localizedMessages.clear();
        simpleMessages.clear();
        doAnswer(invocationOnMock -> {
            List<Object> args = Arrays.asList(invocationOnMock.getArguments());
            args.forEach((e) -> {
                if (!e.equals(KNOWN_EXCEPTION_MESSAGE))
                    throw new NoSuchMessageException("");
                localizedMessages.add(e.toString().trim());
            });
            return null;
        }).when(ioMessageService).writeLocal(anyString(), any());
        doAnswer(invocationOnMock -> {
            List<Object> args = Arrays.asList(invocationOnMock.getArguments());
            args.forEach((e) -> simpleMessages.add(e.toString().trim()));
            return null;
        }).when(ioMessageService).write(anyString());

    }

    @AfterEach
    void tearDown() {
        reset(ioMessageService);
    }

    @Test
    @DisplayName("Выводить локализованные сообщения, если текст ошибки локализован")
    void handleExceptionWithLocalizeMessage() {
        Exception testException = new Exception(KNOWN_EXCEPTION_MESSAGE);
        exceptionHandler.handle(testException);
        assertThat(localizedMessages).contains(KNOWN_EXCEPTION_MESSAGE);
        assertThat(simpleMessages).hasSize(0);
    }

    @Test
    @DisplayName("Выводить текст ошибки как есть, если он не локализован")
    void handleExceptionWithNotLocalizeMessage() {
        Exception testException = new Exception(UNKNOWN_EXCEPTION_MESSAGE);
        exceptionHandler.handle(testException);
        assertThat(simpleMessages).contains(UNKNOWN_EXCEPTION_MESSAGE);
        assertThat(localizedMessages).hasSize(0);
    }

}