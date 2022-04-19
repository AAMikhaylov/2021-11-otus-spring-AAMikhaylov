package ru.otus.hw13.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw13.domain.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
