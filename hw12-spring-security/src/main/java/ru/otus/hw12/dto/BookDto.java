package ru.otus.hw12.dto;

import lombok.Getter;
import lombok.val;
import ru.otus.hw12.domain.Book;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BookDto {
    private final Long id;
    @NotNull(message = "Выберите автора.")
    private final AuthorDto author;
    @NotBlank(message = "Название не должно быть быть пустым")
    @Size(min = 2, max = 25, message = "Длина названия должна быть от 2 до 50 символов")
    private final String title;
    @Size(max = 1000, message = "Длина краткого содержания должно быть не более 1000 символов")
    private final String shortContent;
    @NotNull(message = "Выберите жанр(ы).")
    private final List<GenreDto> genres;
    private final List<CommentDto> comments;

    public BookDto(Long id, AuthorDto author, String title, String shortContent, List<GenreDto> genres, List<CommentDto> comments) {
        this.id = (id == null ? 0L : id);
        this.author = author;
        this.title = title;
        this.shortContent = shortContent;
        this.genres = genres;
        this.comments = comments;
    }


    public Book toEntity() {
        val genreEntities = genres.stream().map(GenreDto::toEntity).collect(Collectors.toList());
        return new Book(id, author.toEntity(), title, shortContent, genreEntities, null);
    }

    public static BookDto fromEntityEager(Book bookEntity) {
        val genresDto = bookEntity.getGenres()
                .stream()
                .map(GenreDto::fromEntity)
                .collect(Collectors.toList());
        val commentsDto = bookEntity.getComments()
                .stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());
        return new BookDto(bookEntity.getId(), AuthorDto.fromEntity(bookEntity.getAuthor()),
                bookEntity.getTitle(), bookEntity.getShortContent(), genresDto, commentsDto);
    }

    public static BookDto fromEntityLazy(Book bookEntity) {
        val genresDto = bookEntity.getGenres()
                .stream()
                .map(GenreDto::fromEntity)
                .collect(Collectors.toList());
        return new BookDto(bookEntity.getId(), AuthorDto.fromEntity(bookEntity.getAuthor()),
                bookEntity.getTitle(), bookEntity.getShortContent(), genresDto, new ArrayList<>());
    }


}
