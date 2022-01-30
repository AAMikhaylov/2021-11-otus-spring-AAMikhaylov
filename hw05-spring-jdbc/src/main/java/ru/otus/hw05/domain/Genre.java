package ru.otus.hw05.domain;

import lombok.Data;

@Data
public class Genre {
    private final long id;
    private final String name;

    public Genre(long id, String name) {
        this.id = id;
        this.name = name == null ? "" : name.trim();
    }

    public Genre(String name) {
        this(0L, name);
    }
}
