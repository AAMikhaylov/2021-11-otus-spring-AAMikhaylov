package ru.otus.hw08.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;


@NoArgsConstructor
@Getter
@ToString
@Document("comments")
public class Comment {
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
    Book book;

    public Comment(String id, String userName, Book book, String content, Date commentDate) {
        this(id, userName, book, content);
        this.commentDate = commentDate;
    }


    public Comment(String userName, Book book, String content, Date commentDate) {
        this(userName, book, content);
        this.commentDate = commentDate;

    }

    public Comment(String userName, Book book, String content) {
        this.content = content;
        this.userName = userName;
        this.book = book;
    }


    public Comment(String id, String userName, Book book, String content) {
        this.id = id;
        this.content = content;
        this.userName = userName;
        this.book = book;
    }

}

