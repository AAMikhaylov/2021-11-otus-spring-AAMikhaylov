package ru.otus.hw04.io;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;


@SpringBootTest(classes = {IOMessageService.class})
@DisplayName("Методы класса IOMessageService должны:")
class IOMessageServiceTest {
    @MockBean
    IOService ioService;
    @MockBean
    TranslateService translateService;
    @Autowired
    IOMessageService ioMessageService;

    @Test
    @DisplayName("Выводить нелокализованные сообщения")
    void testWrite() {
        ioMessageService.write("Test write string");
        verify(ioService, times(1)).write("Test write string");
    }

    @Test
    @DisplayName("Выводить локализованные сообщения")
    void writeLocal() {
        when(translateService.getMessage(any(), any(), any())).thenAnswer(invocationOnMock -> "Message from " +
                invocationOnMock.getArgument(0) +
                " and with " + invocationOnMock.getArgument(1) + ", " + invocationOnMock.getArgument(2));
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