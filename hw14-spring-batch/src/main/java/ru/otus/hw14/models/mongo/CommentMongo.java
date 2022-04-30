package ru.otus.hw14.models.mongo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import ru.otus.hw14.models.h2.CommentH2;

import java.util.Date;


@NoArgsConstructor
@Getter
@ToString
@Document("comments")
public class CommentMongo {
    @Id
    private String id;
    @Field("content")
    private String content;
    @Field("userName")
    private String userName;
    @Field("commentDate")
    private Date commentDate;
    @DBRef
    @Field("book")
    private BookMongo book;
    @Transient
    private long bookH2Id;

    public CommentMongo(String id, String userName, BookMongo book, String content, Date commentDate, long bookH2Id) {
        this.id = id;
        this.userName = userName;
        this.book = book;
        this.content = content;
        this.commentDate = commentDate;
        this.bookH2Id = bookH2Id;
    }

    public CommentMongo(String userName, String content, Date commentDate, long bookH2Id) {
        this(null, userName, null, content, commentDate, bookH2Id);
    }

    public CommentMongo(String userName, BookMongo book, String content) {
        this(null, userName, book, content, null, 0L);
    }

    public static CommentMongo fromH2(CommentH2 commentH2) {
        return new CommentMongo(commentH2.getUserName(), commentH2.getContent(), commentH2.getCommentDate(), commentH2.getBookId());
    }

    public void setBook(BookMongo book) {
        this.book = book;
    }
}

