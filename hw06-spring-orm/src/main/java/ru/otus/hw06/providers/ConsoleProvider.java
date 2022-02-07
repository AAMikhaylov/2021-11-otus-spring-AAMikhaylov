package ru.otus.hw06.providers;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;

@Component
public class ConsoleProvider implements IOProvider {
    @Override
    public OutputStream getOutStream() {
        return System.out;
    }

    @Override
    public InputStream getInStream() {
        return System.in;
    }
}
