package ru.otus.hw04.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Person {
    private final String name;
    private final String surname;

    @Override
    public String toString() {
        return name + " " + surname;
    }
}
