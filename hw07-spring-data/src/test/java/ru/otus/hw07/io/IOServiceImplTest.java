package ru.otus.hw07.io;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw07.providers.IOProvider;

import java.io.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;


@SpringBootTest(classes = {IOServiceImpl.class})
@DisplayName("Методы класса IOServiceImpl должны:")
class IOServiceImplTest {
    @MockBean
    IOProvider ioProvider;
    @Autowired
    IOService ioServiceImpl;

    @Test
    @DisplayName("Выводить сообщения из заданного стрима")
    void testWrite() throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            when(ioProvider.getOutStream()).thenReturn(out);
            ioServiceImpl.write("Test write message");
            assertThat(out.toString()).isEqualTo("Test write message");
        }
    }

    @Test
    @DisplayName("Считывать сообщения из заданного стрима")
    void testRead() throws IOException {
        try (InputStream in = new ByteArrayInputStream("Test read message".getBytes())) {
            when(ioProvider.getInStream()).thenReturn(in);
            String result = ioServiceImpl.read();
            AssertionsForClassTypes.assertThat(result).isEqualTo("Test read message");
        }
    }
}