package ru.otus.hw09.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw09.domain.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
