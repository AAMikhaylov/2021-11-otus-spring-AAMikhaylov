package ru.otus.hw14.models.h2;

import lombok.Getter;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "comments")
public class CommentH2 {
    @Id
    @Column(name = "id")
    private long id;
    @Column(name = "content")
    private String content;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "comment_date")
    private Timestamp commentDate;
    long bookId;

    @Override
    public String toString() {
        return "CommentH2{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", userName='" + userName + '\'' +
                ", commentDate=" + commentDate +
                '}';
    }
}

