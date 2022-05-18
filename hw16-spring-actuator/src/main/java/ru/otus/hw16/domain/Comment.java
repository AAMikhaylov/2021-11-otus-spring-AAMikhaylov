package ru.otus.hw16.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.sql.Timestamp;


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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Moscow")
    @Column(name = "comment_date", insertable = false, updatable = false)
    private Timestamp commentDate;
    @ManyToOne
    @JoinColumn(name = "book_id", foreignKey = @ForeignKey(name = "fk_book_comment"), nullable = false)
    Book book;

    public Comment(String userName, Book book, String content) {
        this.content = content;
        this.userName = userName;
        this.book = book;
    }

    public Comment(long id, String userName, Book book, String content) {
        this.id = id;
        this.content = content;
        this.userName = userName;
        this.book = book;
    }

}

