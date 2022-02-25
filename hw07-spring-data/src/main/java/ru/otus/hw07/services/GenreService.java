package ru.otus.hw07.services;

import ru.otus.hw07.models.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreService {
    void delete(long id);

    Genre save(Genre genre);

    Optional<Genre> findById(long id);

    List<Genre> findAll();


}
