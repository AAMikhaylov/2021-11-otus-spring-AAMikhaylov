package ru.otus.hw16.domain.projection;

import org.springframework.data.rest.core.config.Projection;
import ru.otus.hw16.domain.Author;
import ru.otus.hw16.domain.Book;

@Projection(name = "authorExcerpt", types = {Author.class})
public interface AuthorExcerpt {

    String getFirstName();

    String getMiddleName();

    String getLastName();

    String getShortName();

    String getFullName();
}
