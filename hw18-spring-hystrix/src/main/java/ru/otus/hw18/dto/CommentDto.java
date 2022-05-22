package ru.otus.hw18.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.val;
import ru.otus.hw18.controllers.deserializer.BookDeserializer;
import ru.otus.hw18.domain.Book;
import ru.otus.hw18.domain.Comment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Getter
public class CommentDto {
    private final Long id;
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    @Size(min = 2, max = 25, message = "Длина имени пользователя должна быть от 2 до 50 символов")
    private final String userName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final Timestamp commentDate;
    @JsonDeserialize(converter = BookDeserializer.class)
    private final BookDto book;
    @NotBlank(message = "Комментарий не должен быть пустым")
    @Size(max = 1000, message = "Длина комментария не более 1000 символов")
    private final String content;

    public CommentDto() {
        this.id = 0L;
        this.userName = "";
        this.commentDate  = null;
        this.book  = null;
        this.content  = "";
    }

    public CommentDto(Long id, String userName, Timestamp commentDate, BookDto book, String content) {
        this.id = (id == null ? 0L : id);
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
        val bookDto = BookDto.fromEntityLazy(comment.getBook());
        return new CommentDto(comment.getId(), comment.getUserName(), comment.getCommentDate(), bookDto, comment.getContent());
    }
}

