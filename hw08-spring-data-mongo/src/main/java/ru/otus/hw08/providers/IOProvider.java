package ru.otus.hw08.providers;

import java.io.InputStream;
import java.io.OutputStream;

public interface IOProvider {
    @SuppressWarnings("SameReturnValue")
    OutputStream getOutStream();
    @SuppressWarnings("SameReturnValue")
    InputStream getInStream();

}
