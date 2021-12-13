package ru.otus.hw03.io;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
@DisplayName("Методы класса IOMessageService должны:")
class IOMessageServiceTest {
    @Configuration
    public static class TestContextConfig {
        @MockBean
        IOService ioService;
        @MockBean
        TranslateService translateService;

        @Bean
        IOMessageService ioMessageService(IOService ioService, TranslateService translateService) {
            return new IOMessageService(ioService, translateService);
        }
    }

    @Autowired
    IOMessageService ioMessageService;
    @Autowired
    IOService ioService;
    @Autowired
    TranslateService translateService;

    @Test
    @DisplayName("Выводить нелокализованные сообщения")
    void testWrite() {
        ioMessageService.write("Test write string");
        verify(ioService, times(1)).write("Test write string");
    }

    @Test
    @DisplayName("Выводить локализованные сообщения")
    void writeLocal() {
        when(translateService.getMessage(any(), any(), any())).thenAnswer(invocationOnMock -> {
            return "Message from " +
                    invocationOnMock.getArgument(0) +
                    " and with " + invocationOnMock.getArgument(1) + ", " + invocationOnMock.getArgument(2);
        });
        ioMessageService.writeLocal("messageid", "arg1", "arg2");
        verify(ioService, times(1)).write("Message from messageid and with arg1, arg2");
    }

    @Test
    @DisplayName("Считывать введенные сообщения")
    void testRead() {
        when(ioService.read()).thenReturn("Test read string");
        String actualString = ioMessageService.read();
        assertThat(actualString).isEqualTo("Test read string");
    }
}