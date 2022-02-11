package ru.otus.hw06.services;

import ru.otus.hw06.models.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    void delete(long id);

    Author save(Author author);

    Optional<Author> findById(long id);

    List<Author> findAll();

}
