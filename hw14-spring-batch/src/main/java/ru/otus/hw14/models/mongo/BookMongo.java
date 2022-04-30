package ru.otus.hw14.models.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import ru.otus.hw14.models.h2.BookH2;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Document("books")
public class BookMongo {
    @Id
    private String id;
    @Field("author")
    @DBRef
    private AuthorMongo author;
    @Field("title")
    private String title;
    @Field("shortContent")
    private String shortContent;
    @Field("genres")
    private List<GenreMongo> genres;
    @Field("h2Id")
    private long h2Id;
    @Transient
    private long authorH2Id;


    public BookMongo(String id, AuthorMongo author, String title, String shortContent, List<GenreMongo> genres, long h2Id, long authorH2Id) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.shortContent = shortContent;
        this.genres = genres;
        this.h2Id = h2Id;
        this.authorH2Id = authorH2Id;
    }


    public BookMongo(String title, String shortContent, List<GenreMongo> genres, long h2Id, long authorH2Id) {
        this(null, null, title, shortContent, genres, h2Id, authorH2Id);
    }

    public BookMongo(AuthorMongo author,String title, String shortContent, List<GenreMongo> genres) {
        this(null, author, title, shortContent, genres, 0L, 0L);
    }

    public void setAuthor(AuthorMongo author) {
        this.author = author;
    }

    public void setGenres(List<GenreMongo> genres) {
        this.genres = genres;
    }

    public static BookMongo fromH2(BookH2 bookH2) {
        val mongoGenres = bookH2.getGenres()
                .stream()
                .map(GenreMongo::fromH2)
                .collect(Collectors.toList());
        return new BookMongo(bookH2.getTitle(), bookH2.getShortContent(), mongoGenres, bookH2.getId(), bookH2.getAuthorId());
    }
}
