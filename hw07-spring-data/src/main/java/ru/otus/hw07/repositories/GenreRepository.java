package ru.otus.hw07.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw07.models.Author;
import ru.otus.hw07.models.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepository  extends JpaRepository<Genre, Long> {
}
