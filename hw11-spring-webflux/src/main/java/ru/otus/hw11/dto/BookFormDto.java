package ru.otus.hw11.dto;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@ToString
public class BookFormDto {
    private final String id;
    @NotNull(message = "Выберите автора.")
    private final String authorId;
    @NotBlank(message = "Название не должно быть быть пустым")
    @Size(min = 2, max = 25, message = "Длина названия должна быть от 2 до 50 символов")
    private final String title;
    @Size(max = 1000, message = "Длина краткого содержания должно быть не более 1000 символов")
    private final String shortContent;
    @NotNull(message = "Выберите жанр(ы).")
    private final List<String> genres;

    public BookFormDto() {
        this.id = null;
        this.authorId = null;
        this.title = "";
        this.shortContent = "";
        this.genres = null;
    }

    public BookFormDto(String id, String authorId, String title, String shortContent, List<String> genres) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.shortContent = shortContent;
        this.genres = genres;
    }


}
