package ru.otus.hw05.dao;

import ru.otus.hw05.domain.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreDao {
    int count();

    long create(Genre genre);

    Optional<Genre> getById(long id);

    List<Genre> getAll();

    List<Genre> getAllUsed();

    int update(Genre genre);

    int deleteById(long id);


}
