package ru.otus.hw13.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import ru.otus.hw13.domain.Book;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(attributePaths = {"author", "genres"})
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    Book findById(long id);

    @EntityGraph(attributePaths = {"author"})
    @PostFilter("hasPermission(filterObject, 'READ')")
    List<Book> findAll();


}
