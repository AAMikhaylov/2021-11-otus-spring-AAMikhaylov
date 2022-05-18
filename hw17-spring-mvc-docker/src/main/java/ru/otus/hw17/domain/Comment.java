package ru.otus.hw17.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;


@NoArgsConstructor
@Getter
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "content")
    private String content;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "comment_date")
    private Timestamp commentDate;
    @ManyToOne
    @JoinColumn(name = "book_id", foreignKey = @ForeignKey(name = "fk_book_comment"), nullable = false)
    Book book;

    public Comment(String userName, Book book, String content) {
        this.content = content;
        this.userName = userName;
        this.book = book;
        this.commentDate = new Timestamp(new Date().getTime());
    }

    public Comment(long id, String userName, Book book, String content) {
        this.id = id;
        this.content = content;
        this.userName = userName;
        this.book = book;
        this.commentDate = new Timestamp(new Date().getTime());
    }

}

