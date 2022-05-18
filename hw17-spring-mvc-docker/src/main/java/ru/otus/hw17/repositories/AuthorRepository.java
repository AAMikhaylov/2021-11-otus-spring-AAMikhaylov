package ru.otus.hw17.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw17.domain.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
