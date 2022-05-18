package ru.otus.hw16.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.otus.hw16.domain.Author;
import ru.otus.hw16.domain.projection.AuthorExcerpt;


@RepositoryRestResource(path = "authors", excerptProjection = AuthorExcerpt.class)
public interface AuthorRepository extends JpaRepository<Author, Long> {
}
