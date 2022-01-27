package ru.otus.hw05.dao;

import ru.otus.hw05.domain.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorDao {
    int count();

    long create(Author author);

    Optional<Author> getById(long id);

    List<Author> getAll();

    int update(Author author);

    int deleteById(long id);
}
