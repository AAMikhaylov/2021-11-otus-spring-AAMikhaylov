package ru.otus.hw16.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.otus.hw16.domain.Book;
import ru.otus.hw16.domain.projection.BookExcerpt;

import java.util.Optional;

@RepositoryRestResource(path = "books", excerptProjection = BookExcerpt.class)
public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(attributePaths = {"author","genres"})
    Optional<Book> findById(Long id);

    @EntityGraph(attributePaths = {"author"})
    Page<Book> findAll(Pageable pageable);


}
