package ru.otus.hw11.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.val;
import ru.otus.hw11.domain.Book;
import ru.otus.hw11.domain.Comment;

import java.util.Date;

@Getter
public class CommentDto {
    private final String id;
    private final String userName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Moscow")
    private final Date commentDate;
    private final BookDto book;
    private final String content;

    public CommentDto() {
        this.id = null;
        this.userName = "";
        this.commentDate = null;
        this.book = null;
        this.content = "";
    }

    public CommentDto(String id, String userName, Date commentDate, BookDto book, String content) {
        this.id = id;
        this.userName = userName;
        this.commentDate = commentDate;
        this.book = book;
        this.content = content;
    }


    public Comment toEntity() {
        Book bookEntity = null;
        if (book != null) {
            bookEntity = book.toEntity();
        }
        return new Comment(id, userName, bookEntity, content);
    }

    public static CommentDto fromEntity(Comment comment) {

        val bookDto = comment.getBook() != null ? BookDto.fromEntity(comment.getBook()) : null;
        return new CommentDto(comment.getId(), comment.getUserName(), comment.getCommentDate(), bookDto, comment.getContent());
    }
}

