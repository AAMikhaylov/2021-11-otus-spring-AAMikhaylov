package ru.otus.hw08.services;

import ru.otus.hw08.models.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreService {
    void delete(String id);

    Genre save(Genre genre);

    Genre cascadeUpdate(Genre genre);


    Optional<Genre> findById(String id);

    List<Genre> findAll();


}
