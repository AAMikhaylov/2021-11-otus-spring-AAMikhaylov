package ru.otus.hw16.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.otus.hw16.domain.Genre;

@RepositoryRestResource(path = "genres")
public interface GenreRepository extends JpaRepository<Genre, Long> {
}
