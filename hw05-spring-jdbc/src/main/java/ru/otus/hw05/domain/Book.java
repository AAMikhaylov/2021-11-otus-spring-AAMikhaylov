package ru.otus.hw05.domain;

import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class Book {
    private final long id;
    private final Author author;
    private final String title;
    private final String shortContent;
    private final List<Genre> genres;


    public Book(long id, Author author, String title, String shortContent, List<Genre> genres) {
        this.id = id;
        this.author = Objects.requireNonNull(author);
        this.title = title;
        this.shortContent = shortContent;
        this.genres = genres;
    }


    public Book(Author author, String title, String shortContent, List<Genre> genres) {
        this(0L, author, title, shortContent, genres);
    }
}
