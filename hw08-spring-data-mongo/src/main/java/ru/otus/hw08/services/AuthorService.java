package ru.otus.hw08.services;

import ru.otus.hw08.models.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    void delete(String id);

    Author save(Author author);

    Optional<Author> findById(String id);

    List<Author> findAll();

}
