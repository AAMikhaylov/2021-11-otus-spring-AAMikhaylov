package ru.otus.hw11.dto;

import lombok.Getter;
import lombok.val;
import ru.otus.hw11.domain.Book;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BookDto {
    private final String id;
    private final AuthorDto author;
    private final String title;
    private final String shortContent;
    private final List<GenreDto> genres;

    public BookDto() {
        this.id = null;
        this.author = null;
        this.title = "";
        this.shortContent = "";
        this.genres = null;
    }

    public BookDto(String id, AuthorDto author, String title, String shortContent, List<GenreDto> genres) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.shortContent = shortContent;
        this.genres = genres;
    }

    public Book toEntity() {
        val genreEntities = genres != null ? genres.stream().map(GenreDto::toEntity).collect(Collectors.toList()) : null;
        val authorEntity = author != null ? author.toEntity() : null;
        return new Book(id, authorEntity, title, shortContent, genreEntities);
    }

    public static BookDto fromEntity(Book bookEntity) {
        val genresDto = bookEntity.getGenres()
                .stream()
                .map(GenreDto::fromEntity)
                .collect(Collectors.toList());
        return new BookDto(bookEntity.getId(), AuthorDto.fromEntity(bookEntity.getAuthor()),
                bookEntity.getTitle(), bookEntity.getShortContent(), genresDto);
    }
}
