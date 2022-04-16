package ru.otus.hw11.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class CommentFormDto {
    private final String id;
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    @Size(min = 2, max = 25, message = "Длина имени пользователя должна быть от 2 до 50 символов")
    private final String userName;
    private final String bookId;
    @NotBlank(message = "Комментарий не должен быть пустым")
    @Size(max = 1000, message = "Длина комментария не более 1000 символов")
    private final String content;

    public CommentFormDto() {
        this.id = null;
        this.userName = "";
        this.bookId = null;
        this.content = "";
    }

    public CommentFormDto(String id, String userName, String bookId, String content) {
        this.id = id;
        this.userName = userName;
        this.bookId = bookId;
        this.content = content;
    }


}
