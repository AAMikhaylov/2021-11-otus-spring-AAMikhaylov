package ru.otus.hw16.domain.projection;

import org.springframework.data.rest.core.config.Projection;
import ru.otus.hw16.domain.Genre;

@Projection(name = "genreExcerpt", types = {Genre.class})
public interface GenreExcerpt {
    String getName();
}
