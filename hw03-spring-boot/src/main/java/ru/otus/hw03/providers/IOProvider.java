package ru.otus.hw03.providers;

import java.io.InputStream;
import java.io.OutputStream;

public interface IOProvider {
    OutputStream getOutStream();
    InputStream getInStream();

}
