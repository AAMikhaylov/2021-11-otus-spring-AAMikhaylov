package ru.otus.hw06.services;

import ru.otus.hw06.models.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreService {
    void delete(long id);

    Genre save(Genre genre);

    Optional<Genre> findById(long id);

    List<Genre> findAll();


}
