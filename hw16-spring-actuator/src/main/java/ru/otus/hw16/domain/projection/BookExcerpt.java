package ru.otus.hw16.domain.projection;

import org.springframework.data.rest.core.config.Projection;
import ru.otus.hw16.domain.Book;

import java.util.List;

@Projection(name = "bookExcerpt", types = {Book.class})
public interface BookExcerpt {
    String getTitle();

    AuthorExcerpt getAuthor();

    String getShortContent();

    List<GenreExcerpt> getGenres();
}
