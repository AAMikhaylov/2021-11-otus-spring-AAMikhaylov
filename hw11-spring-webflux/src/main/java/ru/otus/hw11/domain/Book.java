package ru.otus.hw11.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Document("books")
public class Book {
    @Id
    private String id;
    @Field("author")
    @DBRef
    private Author author;
    @Field("title")
    private String title;
    @Field("shortContent")
    private String shortContent;
    @Field("genres")
    private List<Genre> genres;

    public Book(String id, Author author, String title, String shortContent, List<Genre> genres) {
        this.id = id;
        this.author = Objects.requireNonNull(author);
        this.title = title;
        this.shortContent = shortContent;
        this.genres = genres;
    }

    public Book(Author author, String title, String shortContent, List<Genre> genres) {
        this(null, author, title, shortContent, genres);
    }

}
