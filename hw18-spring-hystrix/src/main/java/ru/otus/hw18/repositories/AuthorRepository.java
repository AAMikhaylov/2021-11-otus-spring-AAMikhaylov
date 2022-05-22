package ru.otus.hw18.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw18.domain.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
