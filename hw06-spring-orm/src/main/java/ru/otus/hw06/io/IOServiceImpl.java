package ru.otus.hw06.io;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw06.providers.IOProvider;

import java.io.PrintStream;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class IOServiceImpl implements IOService {
    private final IOProvider ioProvider;

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
