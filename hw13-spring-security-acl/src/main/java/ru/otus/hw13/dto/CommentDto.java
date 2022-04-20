package ru.otus.hw13.dto;

import lombok.Getter;
import lombok.val;
import ru.otus.hw13.domain.Book;
import ru.otus.hw13.domain.Comment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Getter
public class CommentDto {
    private final Long id;
    private final String userName;
    private final Timestamp commentDate;
    private final BookDto book;
    @NotBlank(message = "Комментарий не должен быть пустым")
    @Size(max = 1000, message = "Длина комментария не более 1000 символов")
    private final String content;

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

