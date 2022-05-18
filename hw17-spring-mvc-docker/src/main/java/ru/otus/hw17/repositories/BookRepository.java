package ru.otus.hw17.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw17.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(attributePaths = {"author", "genres"})
    Optional<Book> findById(long id);

    @EntityGraph(attributePaths = {"author"})
    List<Book> findAll();


}
