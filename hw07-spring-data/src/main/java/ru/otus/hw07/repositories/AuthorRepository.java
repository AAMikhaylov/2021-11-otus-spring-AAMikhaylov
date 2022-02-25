package ru.otus.hw07.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw07.models.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
