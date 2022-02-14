package ru.otus.hw08.repositories;

import ru.otus.hw08.models.Genre;

public interface GenreRepositoryCustom {
    Genre cascadeUpdate(Genre genre);

}
