package ru.otus.hw02.present;

import org.springframework.stereotype.Component;

@Component
public class ConsolePrinter implements UIWriter {
    @Override
    public void write(String message) {
        System.out.print(message);
    }

}
