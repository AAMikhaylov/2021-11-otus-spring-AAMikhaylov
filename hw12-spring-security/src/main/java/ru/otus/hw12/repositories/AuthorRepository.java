package ru.otus.hw12.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw12.domain.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
