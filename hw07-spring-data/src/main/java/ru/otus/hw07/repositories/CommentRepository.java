package ru.otus.hw07.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw07.models.Author;
import ru.otus.hw07.models.Book;
import ru.otus.hw07.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(attributePaths = {"book", "book.author"})
    List<Comment> findAll();

    List<Comment> findAllByBookId(long bookId);


}
