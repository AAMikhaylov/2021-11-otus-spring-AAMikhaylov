package ru.otus.hw08.services;

import ru.otus.hw08.models.Genre;

import java.util.*;

public interface GenreLibraryService {

    void createGenre();

    void outputAllGenres();

    void outputGenreById();

    void updateGenre();

    void deleteGenre();

    List<Genre> selectGenres();

    Optional<Genre> selectGenre();

}
