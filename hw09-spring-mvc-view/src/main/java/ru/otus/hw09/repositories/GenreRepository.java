package ru.otus.hw09.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw09.domain.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
