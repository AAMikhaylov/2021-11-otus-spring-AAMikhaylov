package ru.otus.hw03.io;

import org.springframework.stereotype.Component;
import ru.otus.hw03.providers.IOProvider;

import java.io.PrintStream;
import java.util.Scanner;

@Component
public class IOServiceImpl implements IOService {
    private final IOProvider ioProvider;

    public IOServiceImpl(IOProvider ioProvider) {
        this.ioProvider = ioProvider;
    }

    @Override
    public void write(String message) {
        PrintStream printStream = new PrintStream(ioProvider.getOutStream());
        printStream.print(message);
    }


    @Override
    public String read() {
        Scanner scanner = new Scanner(ioProvider.getInStream());
        return scanner.nextLine();
    }
}
