package ru.otus.hw18.services;

import ru.otus.hw18.dto.GenreDto;

import java.util.List;
import java.util.Optional;

public interface GenreService {
    void delete(long id);

    GenreDto save(GenreDto genre);

    Optional<GenreDto> findById(long id);

    List<GenreDto> findAll();


}
